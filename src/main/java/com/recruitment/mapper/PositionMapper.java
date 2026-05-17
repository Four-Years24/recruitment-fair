package com.recruitment.mapper;

import com.recruitment.entity.Position;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PositionMapper {

    int batchInsert(@Param("list") List<Position> list);

    List<Position> findByRegistrationId(Long registrationId);

    int deleteByRegistrationId(Long registrationId);
}
