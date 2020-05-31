package com.zrkworld.sns.notice.pojo;



import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "tb_notice")
public class Notice implements Serializable {

	@Id
	private String id;//ID

	private String receiverId;//接收消息的用户ID
	private String operatorId;//进行操作的用户ID

	//@TableField(exist = false)表示该字段在表中不存在，需要此字段时从别的数据库多表查询在放进来
	private String operatorName;//进行操作的用户昵称
	private String action;//操作类型（评论，点赞等）
	private String targetType;//对象类型（评论，点赞等）

	//@TableField(exist = false)
	private String targetName;//对象名称或简介
	private String targetId;//对象id
	private Date createtime;//创建日期
	private String type;	//消息类型 sys系统消息  user用户消息
	private String state;	//消息状态（0 未读，1 已读）

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}