package com.finale.bookit.board.model.dao;

import java.util.List;
import java.util.Map;

import com.finale.bookit.board.model.vo.Comment;
import com.finale.bookit.board.model.vo.Community;
import com.finale.bookit.board.model.vo.CommunityAttachment;

public interface CommunityDao {

	
	Map<String, Object> selectCommunityContent(int no);
	
	List<Comment> getCommentList(int no);
	
	List<Comment> getReCommentList(int refNo);
	
	List<CommunityAttachment> getAttachmentList(int no);

	void deleteCommunityContent(int no);
	
	void updateCommunityContent(Map<String, Object> param);

	List<Community> getCommunityList(Map<String, Object> param);

	int getTotalCommunityContent();
	
}