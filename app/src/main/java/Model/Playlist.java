package Model;

public class Playlist {
    String id;
    String name;
    String[] musics;
    Long modyfiDate;
    Long createtionDate;

    public Playlist() {
    }

    public Playlist(String id, String name, String[] musics, Long modyfiDate, Long createtionDate) {
        this.id = id;
        this.name = name;
        this.musics = musics;
        this.modyfiDate = modyfiDate;
        this.createtionDate = createtionDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getMusics() {
        return musics;
    }

    public void setMusics(String[] musics) {
        this.musics = musics;
    }

    public Long getModyfiDate() {
        return modyfiDate;
    }

    public void setModyfiDate(Long modyfiDate) {
        this.modyfiDate = modyfiDate;
    }

    public Long getCreatetionDate() {
        return createtionDate;
    }

    public void setCreatetionDate(Long createtionDate) {
        this.createtionDate = createtionDate;
    }
}
