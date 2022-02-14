package com.finale.bookit.search.model.vo;

import java.io.Serializable;
import java.util.Date;

import com.finale.bookit.member.model.vo.Member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookReview extends BookReviewEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private Member member;

	public BookReview(int reviewNo, String isbn13, String content, int rating, String writer, Date regDate,
			String deleteYn, Member member) {
		super(reviewNo, isbn13, content, rating, writer, regDate, deleteYn);
		this.member = member;
	}
	
	
}

