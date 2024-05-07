package bamv.training.microposts.dao;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import bamv.training.microposts.entity.TFollow;
import bamv.training.microposts.entity.UserlistItem;
import bamv.training.microposts.service.FollowService;;

@Service
public class UserListDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private FollowService followService;

    @Autowired
    public UserListDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    //ユーザ一覧の取得
    public List<UserlistItem> listUser(String id , int page){
        String query = "SELECT * FROM M_USER WHERE USER_ID <> ? LIMIT 2 OFFSET ?";
        List<Map<String,Object>> result = jdbcTemplate.queryForList(query,id,(page-1)*2);
        List<UserlistItem> userlistItem = result.stream().map(
            (Map<String,Object> row) -> new UserlistItem(row.get("user_id").toString(),
                                                         row.get("name").toString(),
                                                         followService.findFollowNumber(row.get("user_id").toString()),
                                                         followService.findFollowerNumber(row.get("user_id").toString()),
                                                         followStatus(id, row.get("user_id").toString()))
        ).toList();

        return userlistItem;
    }

    //他ユーザへのフォロー状態を取得 result=1(フォロー中)　result=0(フォローしていない)
    public int followStatus(String currentUser,String userId){
        String query = "SELECT COUNT(*) FROM T_FOLLOW WHERE FOLLOWING_USER_ID = '"+currentUser+ "' AND FOLLOWED_USER_ID = '"+userId+"'";
        int result = jdbcTemplate.queryForObject(query,Integer.class);
        return result;
    }

    //フォローステータスで,follow操作またはunfollow操作を呼び出す
    public void followAction(String followingid,String followedid,String status){
        TFollow tFollow =  new TFollow();
        tFollow.setFollowId(UUID.randomUUID().toString().substring(0,10));
        tFollow.setFollowingUserId(followingid);
        tFollow.setFollowedUserId(followedid);
        int followstatus = Integer.parseInt(status);

        if(followstatus==0){
            follow(tFollow);            
        }else{
            unfollow(tFollow);
        }
    }

    //他ユーザへのfollowを行う(T_FOLLOWテーブルへINSERT) 
    public void follow(TFollow tFollow){
        SqlParameterSource param = new BeanPropertySqlParameterSource(tFollow);
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate).withTableName("T_FOLLOW");
        insert.execute(param);
    }

    //follow済みユーザをunfollowにする(T_FOLLOWテーブルからDELETE)
    public void unfollow(TFollow tFollow){
        int number = jdbcTemplate.update("DELETE FROM T_FOLLOW WHERE FOLLOWING_USER_ID = ? AND FOLLOWED_USER_ID = ?",
                                         tFollow.getFollowingUserId(),
                                         tFollow.getFollowedUserId());
    }

}
