package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.FolderResponse;
import com.sparta.myselectshop.entity.Folder;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    public void addFolders(List<String> folderNames, User user) {
        List<Folder> existFolderList = folderRepository.findAllByUserAndNameIn(user, folderNames);
        List<Folder> folderList = new ArrayList<>();

        for (String folderName : folderNames) {
            if (isExistFolderName(folderName, existFolderList)) {
                throw new IllegalArgumentException("중복된 폴더명을 제거해주세요! 폴더명: " + folderName);
            }
            Folder folder = new Folder(folderName, user);
            folderList.add(folder);
        }

        folderRepository.saveAll(folderList);
    }

    public List<FolderResponse> getFolders(User user) {
        List<Folder> folderList = folderRepository.findAllByUser(user);

        return folderList.stream()
                .map(FolderResponse::new)
                .toList();
    }

    private boolean isExistFolderName(String folderName, List<Folder> existFolderList) {
        for (Folder existFolder : existFolderList) {
            if (folderName.equals(existFolder.getName())) {
                return true;
            }
        }
        return false;
    }

}
