package com.yang.guessmusic.bean;

public class Song {
	private String songName;
	private String songFileName;
	private int songNameLength;
	
	public Song() {
		super();
	}
	
	public Song(String songName, String songFileName, int songNameLength) {
		super();
		this.songName = songName;
		this.songFileName = songFileName;
		this.songNameLength = songNameLength;
	}

	public String getSongName() {
		return songName;
	}
	public void setSongName(String songName) {
		this.songName = songName;
		this.songNameLength = songName.length();
	}
	public String getSongFileName() {
		return songFileName;
	}
	public void setSongFileName(String songFileName) {
		this.songFileName = songFileName;
	}
	public int getSongNameLength() {
		return songNameLength;
	}
	public char[] getNameCharacters(){
		return songName.toCharArray();
	}
}
