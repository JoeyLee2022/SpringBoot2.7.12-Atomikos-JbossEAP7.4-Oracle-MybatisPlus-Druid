package com.joeylee.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.joeylee.domain.entity.UsrActivityDtl;
import com.joeylee.mapper.primary.PrimaryUsrActivityDtlMapper;
import com.joeylee.mapper.second.SecondUsrActivityDtlMapper;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 多数据源 服务类
 *
 * @author joeylee
 */
@Service
public class MultiDataSourceService {

  @Autowired(required = false)
  private PrimaryUsrActivityDtlMapper db1;

  @Autowired(required = false)
  private SecondUsrActivityDtlMapper db2;

  /**
   * atomikos效果：分布式事物。两个数据库都插入值
   *
   * @return
   */
  @Transactional
  public String insert(UsrActivityDtl dtl) {
    // 注意id自增问题，此处仅作为演示
    db1.insert(dtl);
    dtl.setId(null);
    db2.insert(dtl);
    return "OK";
  }

  /**
   * atomikos效果：分布式事物。 演示发生异常分布式事物回滚 这里无论error 1、2、3，任何一处发生异常，分布式事物都会回滚
   *
   * @return
   */
  @Transactional
  public String insertWithException(UsrActivityDtl dtl) {
    insert(dtl);
    int i = 1 / 0;
    return "OK";
  }

  /**
   * 多数据源 查询
   *
   * @return
   */
  public Map<String, Long> query() {
    Map<String, Long> map = new HashMap<>();
    Long selectCount = db1.selectCount(new QueryWrapper<>());
    map.put("db1", selectCount);
    selectCount = db2.selectCount(new QueryWrapper<>());
    map.put("db2", selectCount);
    return map;
  }


}