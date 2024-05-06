package bamv.training.microposts.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import bamv.training.microposts.controller.MicropostsController.userlistItem;
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

    public List<userlistItem> listUser(String id , int page){
        String query = "SELECT * FROM M_USER WHERE USER_ID <> ? LIMIT 2 OFFSET ?";
        List<Map<String,Object>> result = jdbcTemplate.queryForList(query,id,(page-1)*2);
        List<userlistItem> userlistItem = result.stream().map(
            (Map<String,Object> row) -> new userlistItem(row.get("user_id").toString(),
                                                         row.get("name").toString(),
                                                         followService.findFollowNumber(row.get("user_id").toString()),
                                                         followService.findFollowerNumber(row.get("user_id").toString()),
                                                         followStatus(id, row.get("user_id").toString()))
        ).toList();

        return userlistItem;
    }

    public int followStatus(String currentUser,String userId){
        String query = "SELECT COUNT(*) FROM T_FOLLOW WHERE FOLLOWING_USER_ID = '"+currentUser+ "' AND FOLLOWED_USER_ID = '"+userId+"'";
        int result = jdbcTemplate.queryForObject(query,Integer.class);
        return result;
    }

}
