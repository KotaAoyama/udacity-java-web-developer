package com.udacity.jwdnd.course1.cloudstorage.service;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class NoteService {

    private final UserService userService;
    private final NoteMapper noteMapper;

    public NoteService(UserService userService, NoteMapper noteMapper) {
        this.userService = userService;
        this.noteMapper = noteMapper;
    }

    public List<Note> getNotes(String userName) {

        User user = userService.getUser(userName);
        if (Objects.isNull(user)) {
            throw new RuntimeException(String.format("User is Not Found by the userName, %s", userName));
        }

        return noteMapper.getNotes(user.getUserId());
    }

    public int createNote(String noteTitle,
                          String noteDescription,
                          String userName) {

        User user = userService.getUser(userName);
        if (Objects.isNull(user)) {
            throw new RuntimeException(String.format("User is Not Found by the userName, %s", userName));
        }

        return noteMapper.insert(new Note(
                null,
                noteTitle,
                noteDescription,
                user.getUserId()
        ));
    }

    public Note getNoteById(Integer noteId) {
        return noteMapper.getNoteById(noteId);
    }

    public int deleteNote(Integer noteId) {
        return noteMapper.delete(noteId);
    }

    public int updateNote(Note note) {
        return noteMapper.update(note);
    }

    public boolean isNoteNotAllowed(Note targetNote, String userName) throws Exception {
        User user = userService.getUser(userName);
        if (Objects.isNull(user)) {
            throw new Exception(String.format("User is Not Found by the userName, %s", userName));
        }

        return targetNote.getUserId() != user.getUserId();
    }
}
