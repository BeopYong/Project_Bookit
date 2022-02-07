package com.finale.bookit.admin.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.finale.bookit.admin.model.service.AdminService;
import com.finale.bookit.admin.model.vo.AdminInquire;
import com.finale.bookit.admin.model.vo.Chart;
import com.finale.bookit.common.util.BookitUtils;
import com.finale.bookit.inquire.model.vo.Inquire;
import com.finale.bookit.member.model.vo.MemberEntity;
import com.finale.bookit.report.model.vo.ReportBoard;
import com.finale.bookit.report.model.vo.ReportUser;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;
	
	
	
	
	@GetMapping("/selectChartDay")
	@ResponseBody
	public List<Chart> selectChartDay(@RequestParam String month,Model model) {
		

		List<Chart> chartDay = adminService.selectChartDay(month);
		
		log.debug("chartDay = {}",chartDay);
		
		return chartDay;
		
	}
	@GetMapping("/selectChartMonth")
	@ResponseBody
	public String selectChartMonth(@RequestParam String category,Model model) {
		String month = "month";
		log.debug("category = {}",category);
		if(category.equals(month)) {
			model.addAttribute("category", month);
		};
		
		return category;
		
	}
	
	@GetMapping("/admin.do")
	public void adminPage() {}
	
	@GetMapping("/chart.do")
	public void chart(Model model) {
		
		List<Chart> chart = adminService.selectChart();
		int[] arr = new int[chart.size()];
		for(int i = 0 ; i < chart.size(); i++) {
			arr[i] = chart.get(i).getCount();
		}
		
		model.addAttribute("arr", arr);
		model.addAttribute("size", chart.size());
		
		
	}
	
	// 문의 목록
	@GetMapping("/adminInquireList.do")
	public void adminInquireList(
			@RequestParam(defaultValue = "1") int cPage,
			HttpServletRequest request,
			Model model) {
		
		// 페이지 당 게시글 갯수
		int limit = 5;
		int offset = (cPage - 1) * limit;
		Map<String, Object> param = new HashMap<>();
		param.put("offset", offset);
		param.put("limit", limit);
		
		List<Inquire> inquireList = adminService.selectAllInquire(param);
		log.debug("inquireList = {}", inquireList);
		
		// 페이지 영역
		int totalContent = adminService.selectTotalInquire();
		String url = request.getRequestURI();
		String pagebar = BookitUtils.getPagebar(cPage, limit, totalContent, url);
		
		model.addAttribute("inquireList", inquireList);
		model.addAttribute("pagebar", pagebar);
	}
	
	// 문의 내용 상세보기
	@GetMapping("/adminInquireDetail.do")
	public void adminInquireDetail(@RequestParam int no, Model model) {
		// 사용자 문의글
		Inquire inquire = adminService.selectOneInquire(no);
		
		// 문의에 대한 관리자 답변(게시글 번호를 넘겨주어 조회)
		AdminInquire adminInquire = adminService.selectOneAdminInquire(no);
		
		model.addAttribute("inquire", inquire);
		model.addAttribute("adminInquire", adminInquire);
	}

	// 관리자 답변 등록
	@PostMapping("/inquireAdminReply.do")
	public String inquireAdminReply(RedirectAttributes redirectAttr, AdminInquire adminInquire) {
		int result = adminService.insertAdminReply(adminInquire);
		log.debug("result = {}", result);
		redirectAttr.addFlashAttribute("msg", result > 0 ? "답변이 등록되었습니다." : "다시 시도하세요.");

		return "redirect:/admin/adminInquireDetail.do?no=" + adminInquire.getInquireNo();
	}
	
	// 사용자 신고 상태 변경(승인 = 1, 반려 = 2)
	@PostMapping("/reportUserUpdateCondition.do")
	public String reportUserUpdateCondition(@RequestParam int condition, @RequestParam int no, RedirectAttributes redirectAttr) {
		Map<String, Object> param = new HashMap<>();
		param.put("condition", condition);
		param.put("no", no);
		
		int result = adminService.updateReportUserCondition(param);
		log.debug("result = {}", result);
		if(condition > 1)
			redirectAttr.addFlashAttribute("msg", result > 0 ? "신고가 반려되었습니다." : "다시 시도하세요.");
		else
			redirectAttr.addFlashAttribute("msg", result > 0 ? "신고가 접수되었습니다." : "다시 시도하세요.");
		
		return "redirect:/admin/adminReportList.do";
	}
	
	// 게시글 신고 상태 변경(승인 = 1, 반려 = 2)
	@PostMapping("/reportBoardUpdateCondition.do")
	public String reportBoardUpdateCondition(@RequestParam int condition, @RequestParam int no, RedirectAttributes redirectAttr) {
		Map<String, Object> param = new HashMap<>();
		param.put("condition", condition);
		param.put("no", no);
		
		int result = adminService.updateReportBoardCondition(param);
		log.debug("result = {}", result);
		if(condition > 1)
			redirectAttr.addFlashAttribute("msg", result > 0 ? "신고가 반려되었습니다." : "다시 시도하세요.");
		else
			redirectAttr.addFlashAttribute("msg", result > 0 ? "신고가 접수되었습니다." : "다시 시도하세요.");
		
		return "redirect:/admin/adminReportList.do";
	}
	
	// 신고 목록
	@GetMapping("/adminReportList.do")
	public void reportList(
			@RequestParam(defaultValue = "1") int cPage,
			HttpServletRequest request,
			Model model) {
		
		// 페이지 당 게시글 갯수
		int limit = 5;
		int offset = (cPage - 1) * limit;
		Map<String, Object> param = new HashMap<>();
		param.put("offset", offset);
		param.put("limit", limit);
		
		// 사용자 신고 목록
		List<ReportUser> reportUserList = adminService.selectAllReportUser(param);
		// 게시글 신고 목록
		List<ReportBoard> reportBoardList = adminService.selectAllReportBoard(param);
		log.debug("reportUserList = {}", reportUserList);
		log.debug("reportBoardList = {}", reportBoardList);
		
		// 페이지 영역
		int totalContent = adminService.selectTotalReport();
		String url = request.getRequestURI();
		String pagebar = BookitUtils.getPagebar(cPage, limit, totalContent, url);
		
		model.addAttribute("reportUserList", reportUserList);
		model.addAttribute("reportBoardList", reportBoardList);
		model.addAttribute("pagebar", pagebar);
	}
	
	// 회원 삭제
	@PostMapping("/deleteUser.do")
	public String deleteUser(@RequestParam String reportee, RedirectAttributes redirectAttr) {
		int result = adminService.deleteUser(reportee);
		redirectAttr.addFlashAttribute("msg", result > 0 ? "회원이 삭제되었습니다." : "다시 시도하세요.");
		
		return "redirect:/admin/adminReportList.do";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
