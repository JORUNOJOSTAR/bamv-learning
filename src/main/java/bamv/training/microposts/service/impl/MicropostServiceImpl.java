package bamv.training.microposts.service.impl;

import bamv.training.microposts.dao.MUserDao;
import bamv.training.microposts.dao.TMicropostDao;
import bamv.training.microposts.dto.MicropostDto;
import bamv.training.microposts.service.MicropostService;
import bamv.training.microposts.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MicropostServiceImpl implements MicropostService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private TMicropostDao tMicropostDao;

    @Autowired
    private MUserDao mUserDao;

    @Override
    public int countMicropostNumber(String userId) {
        return tMicropostDao.countMicropostNumber(userId);
    }

    @Override
    public List<MicropostDto> searchFollowMicropost(String userId, int page) {
        return tMicropostDao.searchFollowingMicropost(userId, page)
                .stream().map(it ->
                        new MicropostDto(
                                it.getUserId(),
                                mUserDao.findUser(it.getUserId()).getName(),
                                it.getContent(),
                                it.getPostedDatetime()
                        )
                ).toList();
    }

    @Override
    public List<MicropostDto> searchUserMicropost(String userId, int page) {
        return tMicropostDao.searchUserMicropost(userId, page)
                .stream().map(it ->
                        new MicropostDto(
                                it.getUserId(),
                                mUserDao.findUser(it.getUserId()).getName(),
                                it.getContent(),
                                it.getPostedDatetime()
                        )
                ).toList();
    }

    @Override
    @Transactional
    public int createNewMicropost(String userId, String content) {
        return tMicropostDao.addNewMicropost(userId, content);
    }
}
