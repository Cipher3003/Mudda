package com.mudda.backend.media;

import java.util.List;

import com.mudda.backend.media.dto.BatchImageUploadResponse;
import com.mudda.backend.media.dto.ImageUploadResponse;
import com.mudda.backend.media.dto.MediaUploadRequest;
import com.mudda.backend.media.dto.MediaUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {

    List<String> getImages();

    ImageUploadResponse uploadImage(MultipartFile file);

    BatchImageUploadResponse uploadImages(List<MultipartFile> files);

    MediaUploadResponse initUpload(MediaUploadRequest request);

    List<MediaUploadResponse> initUploads(List<MediaUploadRequest> requests);

    void completeUpload(String id);

    void linkToIssue(long issueId, List<String> mediaKeys);

    void linkToUser(long userId, String mediaKey);

    void deleteImage(String imageFileName);

}
