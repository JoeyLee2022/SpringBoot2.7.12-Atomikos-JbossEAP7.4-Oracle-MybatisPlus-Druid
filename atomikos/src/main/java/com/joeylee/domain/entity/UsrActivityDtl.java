package com.joeylee.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@TableName("TB_USR_ACTIVITY_DTL")
public class UsrActivityDtl {

  @TableId(value = "ID", type = IdType.AUTO)
  private Long id;

  @TableField(value = "USR_ACT_ID")
  private Long usrActId;

  @TableField("NAME")
  private String name;

  @TableField("PATH")
  private String path;

  @TableField("VALUE")
  private String value;

  @TableField("IS_ENC")
  private String isEnc;

  @TableField("CREATE_BY")
  private String create_by;

  @TableField("CREATE_DATE")
  private Date create_date;

  @TableField("UPDATE_BY")
  private String update_by;

  @TableField("UPDATE_DATE")
  private Date update_date;

  public UsrActivityDtl(Long usrActId, String name, String value, String isEnc) {
    this.usrActId = usrActId;
    this.name = name;
    this.value = value;
    this.isEnc = isEnc;
  }

  public UsrActivityDtl() {
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUsrActId() {
    return usrActId;
  }

  public void setUsrActId(Long usrActId) {
    this.usrActId = usrActId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getIsEnc() {
    return isEnc;
  }

  public void setIsEnc(String isEnc) {
    this.isEnc = isEnc;
  }

  public Date getCreate_date() {
    return create_date;
  }

  public void setCreate_date(Date create_date) {
    this.create_date = create_date;
  }

  public String getUpdate_by() {
    return update_by;
  }

  public void setUpdate_by(String update_by) {
    this.update_by = update_by;
  }

  public Date getUpdate_date() {
    return update_date;
  }

  public void setUpdate_date(Date update_date) {
    this.update_date = update_date;
  }

  public String getCreate_by() {
    return create_by;
  }

  public void setCreate_by(String create_by) {
    this.create_by = create_by;
  }

  @Override
  public String toString() {
    return "UsrActivityDtl{" +
        "id=" + id +
        ", usrActId=" + usrActId +
        ", name='" + name + '\'' +
        ", path='" + path + '\'' +
        ", value='" + value + '\'' +
        ", isEnc='" + isEnc + '\'' +
        ", create_by='" + create_by + '\'' +
        ", create_date=" + create_date +
        ", update_by='" + update_by + '\'' +
        ", update_date=" + update_date +
        '}';
  }
}
