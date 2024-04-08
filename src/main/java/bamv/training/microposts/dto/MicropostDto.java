package bamv.training.microposts.dto;

import java.time.LocalDateTime;

public class MicropostDto {

    private String userId;

    private String name;

    private String content;

    private LocalDateTime postedDatetime;

    public MicropostDto(String userId, String name, String content, LocalDateTime postedDatetime) {
        this.userId = userId;
        this.name = name;
        this.content = content;
        this.postedDatetime = postedDatetime;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getPostedDatetime() {
        return postedDatetime;
    }

    public void setPostedDatetime(LocalDateTime postedDatetime) {
        this.postedDatetime = postedDatetime;
    }
}