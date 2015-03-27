/*
 * South Face Software
 * Copyright 2012, South Face Software, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.sfs.ucm.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.sfs.ucm.data.Literal;

/**
 * SurvivalTest
 * 
 * @author lbbishop
 * 
 */
@Entity
@Audited
@Table(name="survivaltest")
public class SurvivalTest extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "size_multiplier", nullable = false)
	private BigDecimal sizeMultiplier;

	@NotNull
	@Column(name = "final_score", nullable = false)
	private BigDecimal finalScore;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question1;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question2;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question3;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question4;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question5;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question6;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question7;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question8;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question9;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question10;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question11;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question12;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question13;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question14;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question15;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question16;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question17;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question18;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question19;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question20;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question21;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question22;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question23;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question24;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question25;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question26;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question27;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question28;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question29;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question30;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question31;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question32;

	@NotNull(message = "Answer is required")
	@Size(max = 16)
	@Column(length = 16, nullable = false)
	private String question33;

	@OneToOne(mappedBy = "survivalTest")
	private Stakeholder stakeholder;

	/**
	 * Default constructor
	 */
	public SurvivalTest() {
		super();
	}

	/**
	 * PrePersist method
	 */
	@PrePersist
	public void prePersist() {
		if (this.modifiedBy == null) {
			this.modifiedBy = Literal.APPNAME.toString();
		}
		this.modifiedDate = new Date();
	}

	/**
	 * PreUpdate method
	 */
	@PreUpdate
	public void preUpdate() {
		if (this.modifiedBy == null) {
			this.modifiedBy = Literal.APPNAME.toString();
		}
		this.modifiedDate = new Date();
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the sizeMultiplier
	 */
	public BigDecimal getSizeMultiplier() {
		return sizeMultiplier;
	}

	/**
	 * @param sizeMultiplier
	 *            the sizeMultiplier to set
	 */
	public void setSizeMultiplier(BigDecimal sizeMultiplier) {
		this.sizeMultiplier = sizeMultiplier;
	}

	/**
	 * @return the finalScore
	 */
	public BigDecimal getFinalScore() {
		return finalScore;
	}

	/**
	 * @param finalScore
	 *            the finalScore to set
	 */
	public void setFinalScore(BigDecimal finalScore) {
		this.finalScore = finalScore;
	}

	/**
	 * @return the question1
	 */
	public String getQuestion1() {
		return question1;
	}

	/**
	 * @param question1
	 *            the question1 to set
	 */
	public void setQuestion1(String question1) {
		this.question1 = question1;
	}

	/**
	 * @return the question2
	 */
	public String getQuestion2() {
		return question2;
	}

	/**
	 * @param question2
	 *            the question2 to set
	 */
	public void setQuestion2(String question2) {
		this.question2 = question2;
	}

	/**
	 * @return the question3
	 */
	public String getQuestion3() {
		return question3;
	}

	/**
	 * @param question3
	 *            the question3 to set
	 */
	public void setQuestion3(String question3) {
		this.question3 = question3;
	}

	/**
	 * @return the question4
	 */
	public String getQuestion4() {
		return question4;
	}

	/**
	 * @param question4
	 *            the question4 to set
	 */
	public void setQuestion4(String question4) {
		this.question4 = question4;
	}

	/**
	 * @return the question5
	 */
	public String getQuestion5() {
		return question5;
	}

	/**
	 * @param question5
	 *            the question5 to set
	 */
	public void setQuestion5(String question5) {
		this.question5 = question5;
	}

	/**
	 * @return the question6
	 */
	public String getQuestion6() {
		return question6;
	}

	/**
	 * @param question6
	 *            the question6 to set
	 */
	public void setQuestion6(String question6) {
		this.question6 = question6;
	}

	/**
	 * @return the question7
	 */
	public String getQuestion7() {
		return question7;
	}

	/**
	 * @param question7
	 *            the question7 to set
	 */
	public void setQuestion7(String question7) {
		this.question7 = question7;
	}

	/**
	 * @return the question8
	 */
	public String getQuestion8() {
		return question8;
	}

	/**
	 * @param question8
	 *            the question8 to set
	 */
	public void setQuestion8(String question8) {
		this.question8 = question8;
	}

	/**
	 * @return the question9
	 */
	public String getQuestion9() {
		return question9;
	}

	/**
	 * @param question9
	 *            the question9 to set
	 */
	public void setQuestion9(String question9) {
		this.question9 = question9;
	}

	/**
	 * @return the question10
	 */
	public String getQuestion10() {
		return question10;
	}

	/**
	 * @param question10
	 *            the question10 to set
	 */
	public void setQuestion10(String question10) {
		this.question10 = question10;
	}

	/**
	 * @return the question11
	 */
	public String getQuestion11() {
		return question11;
	}

	/**
	 * @param question11
	 *            the question11 to set
	 */
	public void setQuestion11(String question11) {
		this.question11 = question11;
	}

	/**
	 * @return the question12
	 */
	public String getQuestion12() {
		return question12;
	}

	/**
	 * @param question12
	 *            the question12 to set
	 */
	public void setQuestion12(String question12) {
		this.question12 = question12;
	}

	/**
	 * @return the question13
	 */
	public String getQuestion13() {
		return question13;
	}

	/**
	 * @param question13
	 *            the question13 to set
	 */
	public void setQuestion13(String question13) {
		this.question13 = question13;
	}

	/**
	 * @return the question14
	 */
	public String getQuestion14() {
		return question14;
	}

	/**
	 * @param question14
	 *            the question14 to set
	 */
	public void setQuestion14(String question14) {
		this.question14 = question14;
	}

	/**
	 * @return the question15
	 */
	public String getQuestion15() {
		return question15;
	}

	/**
	 * @param question15
	 *            the question15 to set
	 */
	public void setQuestion15(String question15) {
		this.question15 = question15;
	}

	/**
	 * @return the question16
	 */
	public String getQuestion16() {
		return question16;
	}

	/**
	 * @param question16
	 *            the question16 to set
	 */
	public void setQuestion16(String question16) {
		this.question16 = question16;
	}

	/**
	 * @return the question17
	 */
	public String getQuestion17() {
		return question17;
	}

	/**
	 * @param question17
	 *            the question17 to set
	 */
	public void setQuestion17(String question17) {
		this.question17 = question17;
	}

	/**
	 * @return the question18
	 */
	public String getQuestion18() {
		return question18;
	}

	/**
	 * @param question18
	 *            the question18 to set
	 */
	public void setQuestion18(String question18) {
		this.question18 = question18;
	}

	/**
	 * @return the question19
	 */
	public String getQuestion19() {
		return question19;
	}

	/**
	 * @param question19
	 *            the question19 to set
	 */
	public void setQuestion19(String question19) {
		this.question19 = question19;
	}

	/**
	 * @return the question20
	 */
	public String getQuestion20() {
		return question20;
	}

	/**
	 * @param question20
	 *            the question20 to set
	 */
	public void setQuestion20(String question20) {
		this.question20 = question20;
	}

	/**
	 * @return the question21
	 */
	public String getQuestion21() {
		return question21;
	}

	/**
	 * @param question21
	 *            the question21 to set
	 */
	public void setQuestion21(String question21) {
		this.question21 = question21;
	}

	/**
	 * @return the question22
	 */
	public String getQuestion22() {
		return question22;
	}

	/**
	 * @param question22
	 *            the question22 to set
	 */
	public void setQuestion22(String question22) {
		this.question22 = question22;
	}

	/**
	 * @return the question23
	 */
	public String getQuestion23() {
		return question23;
	}

	/**
	 * @param question23
	 *            the question23 to set
	 */
	public void setQuestion23(String question23) {
		this.question23 = question23;
	}

	/**
	 * @return the question24
	 */
	public String getQuestion24() {
		return question24;
	}

	/**
	 * @param question24
	 *            the question24 to set
	 */
	public void setQuestion24(String question24) {
		this.question24 = question24;
	}

	/**
	 * @return the question25
	 */
	public String getQuestion25() {
		return question25;
	}

	/**
	 * @param question25
	 *            the question25 to set
	 */
	public void setQuestion25(String question25) {
		this.question25 = question25;
	}

	/**
	 * @return the question26
	 */
	public String getQuestion26() {
		return question26;
	}

	/**
	 * @param question26
	 *            the question26 to set
	 */
	public void setQuestion26(String question26) {
		this.question26 = question26;
	}

	/**
	 * @return the question27
	 */
	public String getQuestion27() {
		return question27;
	}

	/**
	 * @param question27
	 *            the question27 to set
	 */
	public void setQuestion27(String question27) {
		this.question27 = question27;
	}

	/**
	 * @return the question28
	 */
	public String getQuestion28() {
		return question28;
	}

	/**
	 * @param question28
	 *            the question28 to set
	 */
	public void setQuestion28(String question28) {
		this.question28 = question28;
	}

	/**
	 * @return the question29
	 */
	public String getQuestion29() {
		return question29;
	}

	/**
	 * @param question29
	 *            the question29 to set
	 */
	public void setQuestion29(String question29) {
		this.question29 = question29;
	}

	/**
	 * @return the question30
	 */
	public String getQuestion30() {
		return question30;
	}

	/**
	 * @param question30
	 *            the question30 to set
	 */
	public void setQuestion30(String question30) {
		this.question30 = question30;
	}

	/**
	 * @return the question31
	 */
	public String getQuestion31() {
		return question31;
	}

	/**
	 * @param question31
	 *            the question31 to set
	 */
	public void setQuestion31(String question31) {
		this.question31 = question31;
	}

	/**
	 * @return the question32
	 */
	public String getQuestion32() {
		return question32;
	}

	/**
	 * @param question32
	 *            the question32 to set
	 */
	public void setQuestion32(String question32) {
		this.question32 = question32;
	}

	/**
	 * @return the question33
	 */
	public String getQuestion33() {
		return question33;
	}

	/**
	 * @param question33
	 *            the question33 to set
	 */
	public void setQuestion33(String question33) {
		this.question33 = question33;
	}

	/**
	 * @return the stakeholder
	 */
	public Stakeholder getStakeholder() {
		return stakeholder;
	}

	/**
	 * @param stakeholder
	 *            the stakeholder to set
	 */
	public void setStakeholder(Stakeholder stakeholder) {
		this.stakeholder = stakeholder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SurvivalTest [id=").append(id).append(", sizeMultiplier=").append(sizeMultiplier).append(", finalScore=").append(finalScore).append(", question1=")
				.append(question1).append(", question2=").append(question2).append(", question3=").append(question3).append(", question4=").append(question4).append(", question5=").append(question5)
				.append(", question6=").append(question6).append(", question7=").append(question7).append(", question8=").append(question8).append(", question9=").append(question9)
				.append(", question10=").append(question10).append(", question11=").append(question11).append(", question12=").append(question12).append(", question13=").append(question13)
				.append(", question14=").append(question14).append(", question15=").append(question15).append(", question16=").append(question16).append(", question17=").append(question17)
				.append(", question18=").append(question18).append(", question19=").append(question19).append(", question20=").append(question20).append(", question21=").append(question21)
				.append(", question22=").append(question22).append(", question23=").append(question23).append(", question24=").append(question24).append(", question25=").append(question25)
				.append(", question26=").append(question26).append(", question27=").append(question27).append(", question28=").append(question28).append(", question29=").append(question29)
				.append(", question30=").append(question30).append(", question31=").append(question31).append(", question32=").append(question32).append(", question33=").append(question33)
				.append(", stakeholder=").append(stakeholder).append("]");
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((question1 == null) ? 0 : question1.hashCode());
		result = prime * result + ((question10 == null) ? 0 : question10.hashCode());
		result = prime * result + ((question11 == null) ? 0 : question11.hashCode());
		result = prime * result + ((question12 == null) ? 0 : question12.hashCode());
		result = prime * result + ((question13 == null) ? 0 : question13.hashCode());
		result = prime * result + ((question14 == null) ? 0 : question14.hashCode());
		result = prime * result + ((question15 == null) ? 0 : question15.hashCode());
		result = prime * result + ((question16 == null) ? 0 : question16.hashCode());
		result = prime * result + ((question17 == null) ? 0 : question17.hashCode());
		result = prime * result + ((question18 == null) ? 0 : question18.hashCode());
		result = prime * result + ((question19 == null) ? 0 : question19.hashCode());
		result = prime * result + ((question2 == null) ? 0 : question2.hashCode());
		result = prime * result + ((question20 == null) ? 0 : question20.hashCode());
		result = prime * result + ((question21 == null) ? 0 : question21.hashCode());
		result = prime * result + ((question22 == null) ? 0 : question22.hashCode());
		result = prime * result + ((question23 == null) ? 0 : question23.hashCode());
		result = prime * result + ((question24 == null) ? 0 : question24.hashCode());
		result = prime * result + ((question25 == null) ? 0 : question25.hashCode());
		result = prime * result + ((question26 == null) ? 0 : question26.hashCode());
		result = prime * result + ((question27 == null) ? 0 : question27.hashCode());
		result = prime * result + ((question28 == null) ? 0 : question28.hashCode());
		result = prime * result + ((question29 == null) ? 0 : question29.hashCode());
		result = prime * result + ((question3 == null) ? 0 : question3.hashCode());
		result = prime * result + ((question30 == null) ? 0 : question30.hashCode());
		result = prime * result + ((question31 == null) ? 0 : question31.hashCode());
		result = prime * result + ((question32 == null) ? 0 : question32.hashCode());
		result = prime * result + ((question33 == null) ? 0 : question33.hashCode());
		result = prime * result + ((question4 == null) ? 0 : question4.hashCode());
		result = prime * result + ((question5 == null) ? 0 : question5.hashCode());
		result = prime * result + ((question6 == null) ? 0 : question6.hashCode());
		result = prime * result + ((question7 == null) ? 0 : question7.hashCode());
		result = prime * result + ((question8 == null) ? 0 : question8.hashCode());
		result = prime * result + ((question9 == null) ? 0 : question9.hashCode());
		return result;
	}

}
