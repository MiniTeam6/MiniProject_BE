package com.miniproject.pantry.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import io.sentry.spring.tracing.SentrySpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @SentrySpan
    public String uploadImage(MultipartFile file) throws IOException {
        String fileUri = null;
        String fileName = getFileName(file);
        String contentType = getContentType(fileName.substring(fileName.lastIndexOf(".")));

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(file.getSize());

        // PutObjectRequest 이용하여 파일 생성 없이 바로 업로드
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        // URL 받아올 때 한글 파일명 깨짐 방지
        fileUri = URLDecoder.decode(amazonS3Client.getUrl(bucket, fileName).toString(), StandardCharsets.UTF_8);
        return fileUri;
    }

    @SentrySpan
    public String getFileName(MultipartFile file) {
        // Objects.requireNonNull 을 사용할 수도 있음
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new IllegalArgumentException("file이 존재하지 않습니다.");
        }

        String fileName = file.getOriginalFilename().toLowerCase();
        String uuid = UUID.randomUUID().toString();
        return uuid + "-" + fileName;
    }

    @SentrySpan
    public String getContentType(String extension) {
        String contentType = "";
        // content type을 지정해서 올려주지 않으면 자동으로 "application/octet-stream"으로 고정이 되어
        // 링크 클릭시 웹에서 열리는게 아니라 자동 다운이 시작됨
        switch (extension) {
            case ".png":
                contentType = "image/png";
                break;
            case ".jpg":
            case ".jpeg":
                contentType = "image/jpeg";
                break;
            case ".bmp":
                contentType = "image/bmp";
                break;
            default:
                contentType = "application/octet-stream";
                break;
        }
        return contentType;
    }

    @SentrySpan
    // 이미지 삭제
    public void deleteFile(String fileUrl) {
        // uri에서 파일명만 추출
        amazonS3Client.deleteObject(bucket, fileUrl.split("/")[3]);
    }


    @SentrySpan
    // 이미지 수정 -> 기존 이미지 삭제 후 새 이미지 업로드 ( 더 좋은 방법은? )
    @Transactional
    public String updateImage(String deleteImageUri, String deleteThumbnailUri, MultipartFile newFile) throws IOException {
        // 기존 파일 삭제
        if (deleteImageUri != null) {
            deleteFile(deleteImageUri);
            deleteFile(deleteThumbnailUri);
        }
        // 새 이미지 업로드
        return uploadImage(newFile); // url string 리턴
    }

    @SentrySpan
    // 썸네일 파일 업로드
    public String uploadThumbnail(MultipartFile file, String fileUri) throws IllegalArgumentException, IOException {
        String fileName = fileUri.substring(fileUri.lastIndexOf("/") + 1);
        String contentType = getContentType(fileName.substring(fileName.lastIndexOf(".")));
        // "amazonaws.com/" 이후의 파일명만 추출
        String thumbnailFileName;

        // S3를 위한 썸네일 이미지 만들기
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        BufferedImage thumbnail = Thumbnails.of(bufferedImage).size(600, 600).asBufferedImage();
        ByteArrayOutputStream thumbnailOutput = new ByteArrayOutputStream();
        String imageType = contentType;
        ImageIO.write(thumbnail, Objects.requireNonNull(imageType.substring(imageType.indexOf("/") + 1)), thumbnailOutput);

        // 썸네일 메타데이터
        ObjectMetadata thumbnailMetadata = new ObjectMetadata();
        byte[] thumbnailBytes = thumbnailOutput.toByteArray();
        thumbnailMetadata.setContentLength(thumbnailBytes.length);
        thumbnailMetadata.setContentType(contentType);

        // 썸네일 파일명
        thumbnailFileName = String.valueOf(new StringBuilder(fileName).insert(fileName.lastIndexOf("."), "(thumbnail)"));

        // 썸네일 업로드
        InputStream thumbnailInput = new ByteArrayInputStream(thumbnailBytes);
        amazonS3Client.putObject(new PutObjectRequest(bucket, thumbnailFileName, thumbnailInput, thumbnailMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        thumbnailInput.close();
        thumbnailOutput.close();
        return URLDecoder.decode(amazonS3Client.getUrl(bucket, thumbnailFileName).toString(), StandardCharsets.UTF_8);
    }
}
