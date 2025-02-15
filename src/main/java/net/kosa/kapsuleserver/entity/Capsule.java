package net.kosa.kapsuleserver.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import net.kosa.kapsuleserver.base.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CAPSULES")
@SuperBuilder(toBuilder = true)
public class Capsule extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	private LocalDate unlockDate;

	@Column(nullable = false)
	private String capsuleCode;

	@Column(nullable = false)
	private Float longitude;

	@Column(nullable = false)
	private Float latitude;

	@Column(nullable = false)
	private String title;

	@Lob
	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private Integer capsuleType;

	@ManyToOne
	@JoinColumn(name = "MEMBER_ID", nullable = false)
	private Member member;

	@OneToMany(mappedBy = "capsule", cascade = CascadeType.REMOVE)
	private List<Image> images;

	@OneToMany(mappedBy = "capsule", cascade = CascadeType.REMOVE)
	private List<SharedKey> sharedKeys;

}
