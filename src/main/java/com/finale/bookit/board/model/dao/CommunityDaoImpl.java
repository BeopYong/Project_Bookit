package com.finale.bookit.board.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.finale.bookit.board.model.vo.Comment;
import com.finale.bookit.board.model.vo.Community;
import com.finale.bookit.board.model.vo.CommunityAttachment;

@Repository
public class CommunityDaoImpl implements CommunityDao{

	@Autowired
	private SqlSessionTemplate session;
	
	
	@Override
	public Map<String, Object> selectCommunityContent(int no) {
		return session.selectOne("community.selectCommunityContent", no);
	}

	@Override
	public List<Comment> getCommentList(int no) {
		return session.selectList("community.selectCommunityComment", no);
	}

	@Override
	public List<Comment> getReCommentList(int refNo) {
		return session.selectList("community.selectCommunityReComment", refNo);
	}

	@Override
	public List<CommunityAttachment> getAttachmentList(int no) {
		return session.selectList("community.selectCommunityAttachment", no);
	}

	@Override
	public void deleteCommunityContent(int no) {
		session.update("community.deleteCommunityContent", no);
	}

	@Override
	public void updateCommunityContent(Map<String, Object> param) {
		session.update("community.updateCommunityContent", param);
	}

	@Override
	public List<Community> getCommunityList(Map<String, Object> param) {
		int offset = (int) param.get("offset");
		int limit = (int) param.get("limit");
		RowBounds rowBounds = new RowBounds(offset, limit);
		return session.selectList("community.getCommunityList", null, rowBounds);
	}

	@Override
	public int getTotalCommunityContent() {
		return session.selectOne("community.getTotalCommunityContent");
	}




	
}