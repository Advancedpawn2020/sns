package com.zrkworld.sns.spit.dao;

import com.zrkworld.sns.spit.pojo.Spit;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpitDao extends MongoRepository<Spit, String> {
}
