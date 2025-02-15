package net.kosa.kapsuleserver.controller;

import java.time.LocalDate;
import java.util.List;

import net.kosa.kapsuleserver.entity.Capsule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import net.kosa.kapsuleserver.base.util.LoginUtil;
import net.kosa.kapsuleserver.dto.CapsuleDTO;
import net.kosa.kapsuleserver.dto.CapsuleDetailDTO;
import net.kosa.kapsuleserver.entity.Member;
import net.kosa.kapsuleserver.service.CapsuleService;
import net.kosa.kapsuleserver.service.MemberService;

/**
 * @author dayoung
 * CapsuleController는 타임캡슐과 관련된 요청들을 처리합니다.
 */
@RestController
@RequestMapping("/capsule")
@RequiredArgsConstructor
public class CapsuleController {

	private final MemberService memberService;
	private final CapsuleService capsuleService;
	private final LoginUtil loginUtil;

	/**
     * 타임캡슐 생성
     */
	@PostMapping("/create")
	public ResponseEntity<String> saveCapsule(
		@RequestPart("title") String title,
		@RequestPart("content") String content,
		@RequestPart("unlockDate") String unlockDate,
		@RequestPart("address") String address,
		@RequestPart("latitude") String latitude,
		@RequestPart("longitude") String longitude,
		@RequestPart("kakaoId") String kakaoId,
		@RequestPart(value = "images", required = false) List<MultipartFile> images) {
		try {
			if (kakaoId == null || kakaoId.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("로그인 상태를 확인해주세요.");
			}

			Member member = memberService.getMemberByKakaoId(kakaoId);

			CapsuleDTO capsuleDTO = CapsuleDTO.builder()
				.title(title)
				.content(content)
				.unlockDate(LocalDate.parse(unlockDate))
				.address(address)
				.latitude((float)Double.parseDouble(latitude))
				.longitude((float)Double.parseDouble(longitude))
				.kakaoId(kakaoId)
				.images(images)
				.build();

			capsuleService.saveCapsule(capsuleDTO, member);

			return ResponseEntity.status(HttpStatus.CREATED)
				.body("타임캡슐이 성공적으로 저장되었습니다.");

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("타임캠슐 저장 중 오류가 발생했습니다: " + e.getMessage());
		}
	}

	/**
     * 나의 타임캡슐 조회
     */
	@GetMapping("/list")
	public ResponseEntity<?> findMyCapsule(@RequestParam String kakaoId) {
		try {
			if (kakaoId == null || kakaoId.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body("로그인 상태를 확인해주세요.");
			}

			Long memberId = memberService.getIdByKakaoId(kakaoId);
			List<CapsuleDTO> myCapsule = capsuleService.findMyCapsule(memberId);

			return ResponseEntity.ok(myCapsule);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("타임캠슐 조회 중 오류가 발생했습니다.");
		}
	}

	// 타임캡슐 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteCapsule(@PathVariable Long id,
												@RequestAttribute("kakaoId") String kakaoId) {
		try {
			if (kakaoId == null || kakaoId.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body("로그인 상태를 확인해주세요.");
			}

			Member member = memberService.getMemberByKakaoId(kakaoId);
			capsuleService.deleteCapsule(id, member);

			return ResponseEntity.noContent().build();

		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(e.getMessage());

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("타임캡슐 삭제 중 오류가 발생했습니다.");
		}
	}

	// 타임캡슐 상세 조회
	@GetMapping("/{id}")
	public ResponseEntity<?> getCapsuleDetail(@PathVariable Long id) {
		try {
			if (!loginUtil.isLogin()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body("로그인 상태를 확인해주세요.");
			}

			Member member = loginUtil.getMember();
			CapsuleDetailDTO capsuleDetail = capsuleService.findCapsuleById(id, member);

			return ResponseEntity.ok(capsuleDetail);

		} catch (SecurityException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(e.getMessage());

		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(e.getMessage());

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("타임캡슐 조회 중 오류가 발생했습니다.");
		}
	}
}
