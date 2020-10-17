package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.form.NoteForm;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.service.FileService;
import com.udacity.jwdnd.course1.cloudstorage.service.NoteService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final FileService fileService;
    private final NoteService noteService;

    public HomeController(FileService fileService, NoteService noteService) {
        this.fileService = fileService;
        this.noteService = noteService;
    }


    @GetMapping()
    public String homeView(Authentication auth, Model model) {
        showFiles(auth.getName(), model);
        showNotes(auth.getName(), model);
        return "home";
    }


    @PostMapping("/file/upload")
    public String uploadFile(@RequestParam("fileUpload") MultipartFile fileUpload,
                             Authentication auth,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        String userName = auth.getName();
        if (fileUpload.getSize() == 0) {
            showFiles(userName, model);
            return "redirect:/home";
        }

        String uploadError = null;
        try {
            if (fileService.isFileDuplicated(fileUpload, userName)) {
                uploadError = "File is duplicated.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            uploadError = "There was an error uploading file.";
        }

        if (uploadError == null) {
            try {
                int rowsAdded = fileService.uploadFile(fileUpload, auth.getName());
                if (rowsAdded < 0) {
                    uploadError = "There was an error uploading file.";
                }
            } catch (Exception e) {
                e.printStackTrace();
                uploadError = "There was an error uploading file.";
            }
        }

        if (uploadError == null) {
            return "redirect:/result?uploadSuccess";
        } else {
            redirectAttributes.addAttribute("uploadErrorMessage", uploadError);
            return "redirect:/result?uploadError";
        }
    }


    @GetMapping("/file/{fileId}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Integer fileId, Authentication auth) {

        File targetFile = fileService.getFileById(fileId);
        if (Objects.isNull(targetFile)) {
            return null;
        }

        try {
            if (fileService.isFileNotAllowed(targetFile, auth.getName())) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                        targetFile.getFileName() + "\"")
                .contentType(MediaType.valueOf(targetFile.getContentType()))
                .body(targetFile.getFileData());
    }


    @GetMapping("/file/{fileId}/delete")
    public String deleteFile(@PathVariable Integer fileId,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {

        File targetFile = fileService.getFileById(fileId);
        if (Objects.isNull(targetFile)) {
            return "404";
        }

        String deleteError = null;
        try {
            if (fileService.isFileNotAllowed(targetFile, auth.getName())) {
                return "404";
            }
        } catch (Exception e) {
            e.printStackTrace();
            deleteError = "There was an error deleting file.";
        }

        if (deleteError == null) {
            try {
                int rowsDeleted = fileService.deleteFile(fileId);
                if (rowsDeleted < 0) {
                    deleteError = "There was an error deleting file.";
                }
            } catch (Exception e) {
                e.printStackTrace();
                deleteError = "There was an error deleting file.";
            }
        }

        if (deleteError == null) {
            return "redirect:/result?deleteSuccess";
        } else {
            redirectAttributes.addAttribute("deleteErrorMessage", deleteError);
            return "redirect:/result?deleteError";
        }
    }


    @PostMapping("/note/save")
    public String saveNote(NoteForm noteForm,
                           Authentication auth,
                           RedirectAttributes redirectAttributes) {

        String userName = auth.getName();
        String saveError = null;

        if (Objects.isNull(noteForm.getNoteId())) {
            try {
                int rowsAdded = noteService.createNote(
                        noteForm.getNoteTitle(),
                        noteForm.getNoteDescription(),
                        userName);
                if (rowsAdded < 0) {
                   saveError = "There was an error creating a note.";
                }
            } catch (Exception e) {
                e.printStackTrace();
                saveError = "There was an error creating a note.";
            }

        } else {
            Note targetNote = noteService.getNoteById(noteForm.getNoteId());
            try {
                if (noteService.isNoteNotAllowed(targetNote, userName)) {
                    return "redirect:/404";
                }
                int rowsUpdated = noteService.updateNote(
                        new Note(targetNote.getNoteId(),
                                noteForm.getNoteTitle(),
                                noteForm.getNoteDescription(),
                                targetNote.getUserId()));
                if (rowsUpdated < 0) {
                    saveError = "There was an error updating the note.";
                }
            } catch (Exception e) {
                e.printStackTrace();
                saveError = "There was an error updating the note.";
            }
        }

        if (saveError == null) {
            return "redirect:/result?saveSuccess";
        } else {
            redirectAttributes.addAttribute("saveErrorMessage", saveError);
            return "redirect:/result?saveError";
        }
    }


    @GetMapping("/note/{noteId}/delete")
    public String deleteNote(@PathVariable Integer noteId,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {

        Note targetNote = noteService.getNoteById(noteId);
        if (Objects.isNull(targetNote)) {
            return "404";
        }

        String deleteError = null;
        try {
            if (noteService.isNoteNotAllowed(targetNote, auth.getName())) {
                return "404";
            }
        } catch (Exception e) {
            e.printStackTrace();
            deleteError = "There was an error deleting note.";
        }

        if (deleteError == null) {
            try {
                int rowsDeleted = noteService.deleteNote(noteId);
                if (rowsDeleted < 0) {
                    deleteError = "There was an error deleting note.";
                }
            } catch (Exception e) {
                e.printStackTrace();
                deleteError = "There was an error deleting note.";
            }
        }

        if (deleteError == null) {
            return "redirect:/result?deleteSuccess";
        } else {
            redirectAttributes.addAttribute("deleteErrorMessage", deleteError);
            return "redirect:/result?deleteError";
        }
    }


    private void showFiles(String userName, Model model) {
        List<File> files = fileService.getFiles(userName);
        model.addAttribute("files", files);
    }

    private void showNotes(String userName, Model model) {
        List<Note> notes = noteService.getNotes(userName);
        model.addAttribute("notes", notes);
    }
}
