package com.example.notes.Models;

public class Note {

    String noteText;
    String id;

    public Note(String noteText, String id) {
        this.noteText = noteText;
        this.id = id;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
