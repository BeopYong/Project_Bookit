package com.finale.bookit.member.model.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finale.bookit.board.model.dao.CommunityDao;
import com.finale.bookit.board.model.dao.UsedDao;
import com.finale.bookit.board.model.vo.Posts;
import com.finale.bookit.member.model.dao.MemberDao;
import com.finale.bookit.member.model.vo.Address;
import com.finale.bookit.member.model.vo.MemberEntity;
import com.finale.bookit.search.model.vo.BookReview;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(rollbackFor=Exception.class)
public class MemberServiceImpl implements MemberService {

	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private CommunityDao communityDao;
	
	@Autowired
	private UsedDao usedDao;
	
	@Override
	public int insertMember(MemberEntity member) {
		return memberDao.insertMember(member);
	}
	
	@Override
	public int insertMember(MemberEntity member, Address address) {
		int result = memberDao.insertMember(member);
		if (result > 0) {
			result = insertAddress(address);
		}
		
		return result;
	}

	@Override
	public MemberEntity selectOneMember(String id) {
		return memberDao.selectOneMember(id);
	}
	
	@Override
	public int selectAddress(Address address) {
		return memberDao.selectAddress(address);
	}

	@Override
	public int insertAddress(Address address) {
		return memberDao.insertAddress(address);
	}
	
	@Override
	public int updateAddress(Address address) {
		return memberDao.updateAddress(address);
	}

	@Override
	public int memberUpdate(Map<String, Object> param, Address address) {
		int result = memberDao.memberUpdate(param);
		if (result > 0) {
			if(selectAddress(address) == 0) {
				result = insertAddress(address);
			} else {
				result = updateAddress(address);
			}
		}
		return result;
	}

	@Override
	public int selectOneMemberCount(String id) {
		return memberDao.selectOneMemberCount(id);
	}

	@Override
	public int selectOneMemberNicknameCount(String nickname) {
		return memberDao.selectOneMemberNicknameCount(nickname);
	}

	//????????? ????????????
	@Override
	public String getAccessToken(String code) {
		String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //  URL????????? ???????????? ?????? ??? ??? ??????, POST ?????? PUT ????????? ????????? setDoOutput??? true??? ???????????????.
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //	POST ????????? ????????? ???????????? ???????????? ???????????? ?????? ??????
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=8a451c649411be3540e7cd703568efbf");  // ???????????? key
            sb.append("&redirect_uri=http://localhost:9090/bookit/member/kakao");     // ??????????????? ??????
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //    ????????? ?????? ?????? JSON????????? Response ????????? ????????????
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //    Gson ?????????????????? ????????? ???????????? JSON?????? ?????? ??????
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return access_Token;
    }

	//????????? ??????????????????
	@Override
	public MemberEntity getUserInfo(String access_Token) {
//	    ???????????? ????????????????????? ?????? ????????? ?????? ??? ????????? HashMap???????????? ??????
        HashMap<String, Object> userInfo = new HashMap<String, Object>();
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            //    ????????? ????????? Header??? ????????? ??????
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            String email = kakao_account.getAsJsonObject().get("email").getAsString();
            
            userInfo.put("accessToken", access_Token);
            userInfo.put("nickname", nickname);
            userInfo.put("email", email);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        MemberEntity result = memberDao.findkakao(userInfo);
 		// ??? ????????? ?????? ????????? ?????????????????? ???????????? ??????.
 		System.out.println("S:" + result);
 		if(result==null) {
 		// result??? null?????? ????????? ????????? ????????????????????? ????????? ??????.
 			memberDao.kakaoinsert(userInfo);
 			// ??? ????????? ????????? ???????????? ?????? Repository??? ????????? ?????????.
 			return memberDao.findkakao(userInfo);
 			// ??? ????????? ?????? ?????? ??? ??????????????? ????????? ????????? ?????????.
 			//  result??? ???????????? ????????? null??? ??????????????? ??? ????????? ??????.
 		} else {
 			return result;
 			// ????????? ?????? ?????? ????????? result??? ?????????.
 		}
	}

	@Override
	public int insertAuthority(String id) {
		// TODO Auto-generated method stub
		return memberDao.insertAuthority(id);
	}

	@Override
	public List<BookReview> selectBookReviewList(HashMap<String, Object> param) {
		return memberDao.selectBookReviewList(param);
	}

	@Override
	public int selectTotalBookReviewCountById(HashMap<String, Object> param) {
		return memberDao.selectTotalBookReviewCountById(param);
	}

	@Override
	public int selectMemberCash(HashMap<String, Object> param) {
		return memberDao.selectMemberCash(param);
	}

	@Override
	public int bookReviewDelete(HashMap<String, Object> param) {
		return memberDao.bookReviewDelete(param);
	}
	
	@Override
	public int selectMemberRating(HashMap<String, Object> param) {
		return memberDao.selectMemberRating(param);
	}

	@Override
	public int selectMyPostsTotalCount(String member) {
		return memberDao.selectTotalPosts(member);
	}

	@Override
	public List<Posts> selectMyPosts(String member) {
		return memberDao.selectAllPosts(member);
	}

	@Override
	public void deleteCommunityContent(int no) {
		communityDao.deleteCommunityContent(no);
	}

	@Override
	public void deleteUsedContent(int no) {
		usedDao.deleteUsedContent(no);
	}

	


	
}
