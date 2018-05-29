package pe.com.pucp.dti.volley.Model;

/**
 * Created by DIA on 29/05/18.
 */

public class Post {
    private int id;
    private String title;
    private String body;
    private String userId;

    public Post(int id, String title, String body, String userId) {
        this.setId(id);
        this.setTitle(title);
        this.setBody(body);
        this.setUserId(userId);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
