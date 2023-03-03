package com.atguigu.redis7.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Circle;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther zzyy
 * @create 2022-12-25 12:11
 */
@Service
@Slf4j
public class GeoService
{
    public static final String CITY ="city";
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加坐标geoadd
     * @return
     */
    public String geoAdd()
    {
        Map<String, Point> map = new HashMap<>();
        map.put("天安门",new Point(116.403963,39.915119));
        map.put("故宫",new Point(116.403414,39.924091));
        map.put("长城",new Point(116.024067,40.362639));

        redisTemplate.opsForGeo().add(CITY,map);

        return map.toString();
    }

    /**
     * 获取经纬度坐标geopos
     * @param member
     * @return
     */
    public Point position(String member) {
        //获取经纬度坐标
        List<Point> list = redisTemplate.opsForGeo().position(CITY, member);
        return list.get(0);
    }

    /**
     * 获取经纬度生成的base32编码值geohash
     * @param member
     * @return
     */
    public String hash(String member) {
        //geohash算法生成的base32编码值
        List<String> list = redisTemplate.opsForGeo().hash(CITY, member);
        return list.get(0);
    }

    /**
     * 获取两个给定位置之间的距离
     * @param member1
     * @param member2
     * @return
     */
    public Distance distance(String member1, String member2) {
        //获取两个给定位置之间的距离
        Distance distance = redisTemplate.opsForGeo().distance(CITY, member1, member2,
                RedisGeoCommands.DistanceUnit.KILOMETERS);
        return distance;
    }

    /**
     * 通过经度纬度查找北京王府井附近的
     * @return
     */
    public GeoResults radiusByxy() {
        //通过经度，纬度查找附近的,北京王府井位置116.418017,39.914402
        Circle circle = new Circle(116.418017, 39.914402, Metrics.KILOMETERS.getMultiplier());
        // 返回50条
        RedisGeoCommands.GeoRadiusCommandArgs args =
                RedisGeoCommands.
                        GeoRadiusCommandArgs.
                        newGeoRadiusArgs().
                        includeDistance().
                        includeCoordinates().
                        sortDescending().
                        limit(50);

        GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults = redisTemplate.opsForGeo().radius(CITY, circle, args);

        return geoResults;
    }

    /**
     * 通过地方查找附近,本例写死天安门作为地址,作为家庭作业
     * @return
     */
    public GeoResults radiusByMember() {
        //通过地方查找附近
        String member = "天安门";

//        返回50条
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeCoordinates().includeCoordinates().sortAscending().limit(50);

//        半径10公里内
        Distance distance = new Distance(10, Metrics.KILOMETERS);

        GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults = redisTemplate.opsForGeo().radius(CITY, member, distance, args);

        return geoResults;
    }
}
