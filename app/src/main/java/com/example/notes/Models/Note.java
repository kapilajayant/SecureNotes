package com.example.notes.Models;

public class Note {

    String noteText;
    String id;
    String colorId;

    public Note(String noteText, String id, String colorId) {
        this.noteText = noteText;
        this.id = id;
        this.colorId = colorId;
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

    public String getColorId() {
        return colorId;
    }

    public void setColorId(String colorId) {
        this.colorId = colorId;
    }
}
