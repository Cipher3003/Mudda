package com.mudda.backend.media;

import com.mudda.backend.media.dto.BatchImageUploadResponse;
import com.mudda.backend.media.dto.ImageUploadResponse;
import com.mudda.backend.media.dto.MediaUploadRequest;
import com.mudda.backend.media.dto.MediaUploadResponse;

import java.util.List;

public interface MediaService {

//    TODO: add media deletion event to passively remove images of deleted user

    List<String> getImages();

    ImageUploadResponse uploadImage(MediaFileUploadRequest request);

    BatchImageUploadResponse uploadImages(List<MediaFileUploadRequest> requests);

    MediaUploadResponse initUpload(MediaUploadRequest request);

    List<MediaUploadResponse> initUploads(List<MediaUploadRequest> requests);

    void completeUpload(String id);

    int linkToIssue(long issueId, List<String> mediaKeys);

    int linkToUser(long userId, String mediaKey);

    void deleteImage(String imageFileName);

    void removeImageFromOwner(Long id, MediaOwner mediaOwner);
}
