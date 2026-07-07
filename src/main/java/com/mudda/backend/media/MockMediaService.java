/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : MockImageServiceImpl
 * Author  : Vikas Kumar
 * Created : 11-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.media;

import com.mudda.backend.media.dto.BatchImageUploadResponse;
import com.mudda.backend.media.dto.ImageUploadResponse;
import com.mudda.backend.media.dto.MediaUploadRequest;
import com.mudda.backend.media.dto.MediaUploadResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MockMediaService implements MediaService {

    @PostConstruct
    public void init() {
        log.info("MockImageServiceImpl bean initialized. Mock service is being used.");
    }

    ImageUploadResponse mockResponse = new ImageUploadResponse(
            "originalFile",
            "FileKey",
            "https://someplaceholder.com",
            UploadStatus.SUCCESS,
            null
    );

    @Override
    public List<String> getImages() {
        return List.of();
    }

    @Override
    public ImageUploadResponse uploadImage(MediaFileUploadRequest request) {
        return mockResponse;
    }

    @Override
    public BatchImageUploadResponse uploadImages(List<MediaFileUploadRequest> requests) {
        return new BatchImageUploadResponse(requests.size(), 0, List.of(mockResponse));
    }

    @Override
    public MediaUploadResponse initUpload(MediaUploadRequest request) {
        return null;
    }

    @Override
    public List<MediaUploadResponse> initUploads(List<MediaUploadRequest> requests) {
        return List.of();
    }

    @Override
    public void completeUpload(String id) {
        log.info("MockImageServiceImpl completeUpload. Mock service is being used.");
    }

    @Override
    public int linkToIssue(long issueId, List<String> mediaKeys) {
        return mediaKeys.size();
    }

    @Override
    public int linkToUser(long userId, String mediaKey) {
        return 1;
    }

    @Override
    public void deleteImage(String imageFileName) {
    }

    @Override
    public void removeImageFromOwner(Long id, MediaOwner mediaOwner) {

    }
}
