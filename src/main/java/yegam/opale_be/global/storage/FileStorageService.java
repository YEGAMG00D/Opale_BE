package yegam.opale_be.global.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import yegam.opale_be.domain.culture.performance.exception.PerformanceErrorCode;
import yegam.opale_be.global.exception.CustomException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class FileStorageService {

  private final S3Client s3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  @Value("${cloud.aws.region.static}")
  private String region;

  public String saveFileAndReturnUrl(MultipartFile file, String subDir) {
    try {
      if (file.isEmpty()) {
        throw new CustomException(PerformanceErrorCode.PERFORMANCE_DATA_ACCESS_ERROR);
      }

      String originalName = file.getOriginalFilename();
      String fileName = System.currentTimeMillis() + "_" + originalName;

      String key = subDir + "/" + fileName;

      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(key)
          .contentType(file.getContentType())
          .build();

      s3Client.putObject(
          putObjectRequest,
          RequestBody.fromInputStream(file.getInputStream(), file.getSize())
      );

      String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8);

      return String.format(
          "https://%s.s3.%s.amazonaws.com/%s",
          bucketName, region, encodedKey
      );

    } catch (Exception e) {
      throw new CustomException(PerformanceErrorCode.PERFORMANCE_DATA_ACCESS_ERROR);
    }
  }
}
