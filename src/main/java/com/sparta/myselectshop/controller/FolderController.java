package com.sparta.myselectshop.controller;

import com.sparta.myselectshop.dto.FolderRequest;
import com.sparta.myselectshop.dto.FolderResponse;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.exception.RestApiException;
import com.sparta.myselectshop.security.UserDetailsImpl;
import com.sparta.myselectshop.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FolderController {

    private final FolderService folderService;

    @PostMapping("/folders")
    public void addFolders(@RequestBody FolderRequest requestDto,
                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<String> folderNames = requestDto.getFolderNames();
        User user = userDetails.getUser();

        folderService.addFolders(folderNames, user);
    }

    @GetMapping("/folders")
    public List<FolderResponse> getFolders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return folderService.getFolders(userDetails.getUser());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<RestApiException> handleException(IllegalArgumentException e) {
        RestApiException restApiException = new RestApiException(HttpStatus.BAD_REQUEST.value(), e.getMessage());

        return new ResponseEntity<>(
                restApiException, // HTTP body
                HttpStatus.BAD_REQUEST // HTTP status code
        );
    }

}
