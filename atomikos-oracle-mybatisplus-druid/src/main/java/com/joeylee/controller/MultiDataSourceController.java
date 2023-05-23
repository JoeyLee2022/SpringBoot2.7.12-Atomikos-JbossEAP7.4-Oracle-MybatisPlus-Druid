package com.joeylee.controller;

import com.joeylee.domain.entity.UsrActivityDtl;
import com.joeylee.service.MultiDataSourceService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 多数据 分布式事务 测试接口
 *
 * @author JoeyLee
 */
@RestController
@RequestMapping("/multiDataSource")
public class MultiDataSourceController {

  @Autowired
  private MultiDataSourceService multiDataSourceService;


  @RequestMapping("/insert")
  public String insert(UsrActivityDtl dtl) {
    return multiDataSourceService.insert(dtl);
  }

  @RequestMapping("/insertWithException")
  public String insertWithException(UsrActivityDtl dtl) {
    return multiDataSourceService.insertWithException(dtl);
  }

  @RequestMapping("/query")
  public Map<String, Long> query() {
    return multiDataSourceService.query();
  }

}
