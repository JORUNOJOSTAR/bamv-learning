package bamv.training.microposts.controller;

import bamv.training.microposts.dto.MicropostDto;
import bamv.training.microposts.dto.UserDto;
import bamv.training.microposts.form.MicropostForm;
import bamv.training.microposts.form.UserForm;
import bamv.training.microposts.service.FollowService;
import bamv.training.microposts.service.MicropostService;
import bamv.training.microposts.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MicropostsController {
    @Autowired
    private UserService userService;

    @Autowired
    private MicropostService micropostService;

    @Autowired
    private FollowService followService;

    @GetMapping("/micropostshome")
    String micropostshome(Model model, @ModelAttribute MicropostForm micropostForm, BindingResult bindingResult, HttpServletRequest httpServletRequest, @RequestParam(name = "page", defaultValue = "1") int page) {
        /* ユーザー認証情報からユーザIDを取得 */
        String userId = httpServletRequest.getRemoteUser();

        /* Model ⇔ Controller */
        UserDto user = userService.findUser(userId); // 自ユーザー情報
        int myMicropostNumber = micropostService.countMicropostNumber(userId); // 自ユーザーマイクロポスト数
        List<MicropostDto> followsMicropostList = micropostService.searchFollowMicropost(userId, page); // 自ユーザーおよびフォローのマイクロポスト
        int myFollowNumber = followService.findFollowNumber(userId); // 自ユーザーのフォロー数
        int myFollowerNumber = followService.findFollowerNumber(userId); // 自ユーザーのフォロワー数

        /* View ⇔ Controller */
        model.addAttribute("myUserName", user.getName());
        model.addAttribute("myMicropostsNumber", myMicropostNumber);
        model.addAttribute("myFollowNumber", myFollowNumber);
        model.addAttribute("myFollowerNumber", myFollowerNumber);
        model.addAttribute("followsMicropostList", followsMicropostList);
        model.addAttribute("page", page);

        return "micropostshome";
    }

    @PostMapping("/postnewmicropost")
    String postnewmicropost(Model model, HttpServletRequest httpServletRequest, @ModelAttribute @Valid MicropostForm micropostForm, BindingResult bindingResult) {
        /* ユーザー認証情報からユーザIDを取得 */
        String userId = httpServletRequest.getRemoteUser();

        if (bindingResult.hasErrors())
            return micropostshome(model, micropostForm, bindingResult, httpServletRequest, 1);

        micropostService.createNewMicropost(userId, micropostForm.getContent());

        return "redirect:/micropostshome";
    }

    @GetMapping("/login")
    String login(Model model) {
        return "login";
    }

    @GetMapping("/myprofile")
    String myprofile(Model model, HttpServletRequest httpServletRequest, @RequestParam(name = "page", defaultValue = "1") int page) {
        /* ユーザー認証情報からユーザIDを取得 */
        String userId = httpServletRequest.getRemoteUser();

        /* Model ⇔ Controller */
        UserDto user = userService.findUser(userId); // 自ユーザー情報
        List<MicropostDto> followsMicropostList = micropostService.searchUserMicropost(userId, page); // 自ユーザーのマイクロポスト
        int myFollowNumber = followService.findFollowNumber(userId); // 自ユーザーのフォロー数
        int myFollowerNumber = followService.findFollowerNumber(userId); // 自ユーザーのフォロワー数

        /* View ⇔ Controller */
        model.addAttribute("myUserName", user.getName());
        model.addAttribute("myFollowNumber", myFollowNumber);
        model.addAttribute("myFollowerNumber", myFollowerNumber);
        model.addAttribute("followsMicropostList", followsMicropostList);
        model.addAttribute("page", page);

        return "myprofile";
    }

    @GetMapping("/otherprofile/{userId}")
    String otherprofile(Model model, @PathVariable String userId, @RequestParam(name = "page", defaultValue = "1") int page) {

        /* Model ⇔ Controller */
        UserDto user = userService.findUser(userId); // ユーザー情報
        List<MicropostDto> followsMicropostList = micropostService.searchUserMicropost(userId, page); // ユーザーのマイクロポスト
        int followNumber = followService.findFollowNumber(userId); // ユーザーのフォロー数
        int followerNumber = followService.findFollowerNumber(userId); // ユーザーのフォロワー数

        /* View ⇔ Controller */
        model.addAttribute("userName", user.getName());
        model.addAttribute("followNumber", followNumber);
        model.addAttribute("followerNumber", followerNumber);
        model.addAttribute("followsMicropostList", followsMicropostList);
        model.addAttribute("page", page);

        return "otherprofile";
    }

    @GetMapping("/signup")
    String signup(Model model, @ModelAttribute UserForm userForm, BindingResult bindingResult) {
        return "signup";
    }

    @PostMapping("/signup")
    String postsignup(Model model, @ModelAttribute @Valid UserForm userForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return signup(model, userForm, bindingResult);

        userService.createNewUser(userForm.getUserId(), userForm.getUserName(), userForm.getPassword());

        return "redirect:/login";
    }
}