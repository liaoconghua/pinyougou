package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.*;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.mapper.ItemCatMapper;
import com.pinyougou.sellergoods.mapper.GoodsDescMapper;
import com.pinyougou.sellergoods.mapper.GoodsMapper;
import com.pinyougou.sellergoods.mapper.ItemMapper;
import com.pinyougou.sellergoods.mapper.SellerMapper;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Transactional
@Service(interfaceName = "com.pinyougou.sellergoods.service.GoodsService")
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private SellerMapper sellerMapper;

    /**
     * 添加商品
     */
    @Override
    public boolean save(Goods goods) {
        // 设置未申核状态
        goods.setAuditStatus("0");
        //添加SPU商品表
        int row = goodsMapper.insertSelective(goods);

        // 为商品描述对象设置主键id
        goods.getGoodsDesc().setGoodsId(goods.getId());
        goodsDescMapper.insertSelective(goods.getGoodsDesc());

        //判断是否启用规格
        saveItems(goods);


        return row > 0;
    }

    /**
     * 设置SKU商品信息
     */
    private void setItemInfo(Item item, Goods goods) {
        //设置SKU商品图片地址
        List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (imageList != null && imageList.size() > 0) {
            //取第一张图片
            item.setImage((String) imageList.get(0).get("url"));
        }
        //设置SKU商品的分类(三级分类)
        item.setCategoryid(goods.getCategory3Id());
        //设置SKU商品的创建时间
        item.setCreateTime(new Date());
        //设置SKU商品的修改时间
        item.setUpdateTime(item.getCreateTime());
        //设置SPU商品的编号
        item.setGoodsId(goods.getId());
        //设置商家编号
        item.setSellerId(goods.getSellerId());
        //设置商品分类名称
        item.setCategory(itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName());
        //设置品牌名称
        item.setBrand(brandMapper.selectByPrimaryKey(goods.getBrandId()).getName());
        //设置商家店铺名称
        item.setSeller(sellerMapper.selectByPrimaryKey(goods.getSellerId()).getNickName());
    }

    /**
     * 多条件分页查询商品
     *
     * @param goods 商品对象
     * @param page  当前页码
     * @param rows  每页显示的记录数
     * @return 商品分页数据
     */
    @Override
    public PageResult findByPage(Goods goods, Integer page, Integer rows) {

        PageInfo<Map<String, Object>> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(
                () -> goodsMapper.findAll(goods));

//            循环查询到的商品
        for (Map<String, Object> map : pageInfo.getList()) {
            ItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(map.get("category1Id"));
            map.put("category1Name", itemCat1 != null ? itemCat1.getName() : "");
            ItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(map.get("category2Id"));
            map.put("category2Name", itemCat2 != null ? itemCat2.getName() : "");
            ItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(map.get("category3Id"));
            map.put("category3Name", itemCat3 != null ? itemCat3.getName() : "");
        }

        return new PageResult(pageInfo.getList(), pageInfo.getTotal());
    }

    /**
     * 商品审批，修改商品状态
     */
    @Override
    public boolean updateStatus(Long[] ids, String status) {
        Goods goods = new Goods();
        goods.setAuditStatus(status);

        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));

        int row = goodsMapper.updateByExampleSelective(goods, example);
        return row > 0;
    }

    /**
     * 商品删除，修改商品删除状态
     */
    @Override
    public boolean deleteAll(Long[] ids) {
        Goods goods = new Goods();
        goods.setIsDelete("1");

        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));

        int row = goodsMapper.updateByExampleSelective(goods, example);
        return row > 0;
    }

    /**
     * 商家商品上下架(修改可销售状态)
     */
    @Override
    public boolean updateMarketable(Long[] ids, String status) {
        Goods goods = new Goods();
        goods.setIsMarketable(status);

        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));

        int row = goodsMapper.updateByExampleSelective(goods, example);
        return row > 0;
    }

    /**
     * 查询需修改的商品回显数据
     */
    @Override
    public Goods findGoodsById(Long id) {
        try {
            Goods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));
            // 创建Item对象封装查询条件
            Item item = new Item();
            item.setGoodsId(id);
            List<Item> items = itemMapper.select(item);
            goods.setItems(items);
            return goods;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    /**
     * 修改商品
     */
    @Override
    public boolean update(Goods goods) {
        try {
            // 设置审核状态为：未审核
            goods.setAuditStatus("0");
            // 修改SPU商品表
            goodsMapper.updateByPrimaryKeySelective(goods);
            // 修改商品描述表
            goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());
            // 删除原有SKU具体商品数据
            Item item = new Item();
            item.setGoodsId(goods.getId());
            itemMapper.delete(item);
            // 重新添加SKU具体商品数据
            saveItems(goods);
            return true;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 保存sku具体商品信息
     */
    private void saveItems(Goods goods) {
        // 判断是否启用规格
        if ("1".equals(goods.getIsEnableSpec())) {
            //迭代所有的SKU具体商品集合，往SKU表插入数据
            // 迭代所有的SKU具体商品集合，往SKU表插入数据
            for (Item item : goods.getItems()) {
                // 定义SKU商品的标题
                StringBuilder title = new StringBuilder();
                title.append(goods.getGoodsName());
                // 把规格选项JSON字符串转化成Map集合
                Map<String, Object> spec = JSON.parseObject(item.getSpec());
                for (Object value : spec.values()) {
                    // 拼接规格选项到SKU商品标题
                    title.append(" ").append(value);
                }
                //设置SKU商品的标题
                item.setTitle(title.toString());

                //设置SKU商品其它属性
                setItemInfo(item, goods);
                //一个SKU商品
                itemMapper.insertSelective(item);
            }
        } else {
            // 创建SKU具体商品对象
            Item item = new Item();
            // 设置SKU商品的标题
            item.setTitle(goods.getGoodsName());
            // 设置SKU商品的价格
            item.setPrice(goods.getPrice());
            // 设置SKU商品库存数据
            item.setNum(9999);
            // 设置SKU商品启用状态
            item.setStatus("1");
            // 设置是否默认
            item.setIsDefault("1");
            // 设置规格选项
            item.setSpec("{}");
            // 设置SKU商品其它属性
            setItemInfo(item, goods);
            itemMapper.insertSelective(item);
        }
    }

    /**
     * 根据id查询商品信息
     */
    @Override
    public HashMap<String, Object> getGoods(Long goodsId) {

        try {
            //定义数据模型
            HashMap<String, Object> dataModel = new HashMap<>();
            // 加载商品SPU数据
            Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods", goods);

            //加载商品描述数据
            GoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc", goodsDesc);

            //商品分类
            if (goods != null && goods.getCategory3Id() != null) {
                String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
                String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
                String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
                dataModel.put("itemCat1", itemCat1);
                dataModel.put("itemCat2", itemCat2);
                dataModel.put("itemCat3", itemCat3);
            }

            //查询SKU数据
            Example itemExample = new Example(Item.class);
            Example.Criteria itemExampleCriteria = itemExample.createCriteria();
            itemExampleCriteria.andEqualTo("status", "1");
            itemExampleCriteria.andEqualTo("goodsId", goodsId);
            itemExample.orderBy("isDefault").desc();
            List<Item> itemList = itemMapper.selectByExample(itemExample);
            //将SKU集合数据转换成json字符串后，放入map数据模型中
            dataModel.put("itemList", JSON.toJSONString(itemList));

            return dataModel;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据商品id数组批量查询sku
     *
     * @param ids 商品id数组
     * @return 商品的sku集合
     */
    @Override
    public List<Item> findItemByGoodsId(Long[] ids) {
        try {
            Example itemExample = new Example(Item.class);
            Example.Criteria itemExampleCriteria = itemExample.createCriteria();
            itemExampleCriteria.andIn("goodsId", Arrays.asList(ids));
            return itemMapper.selectByExample(itemExample);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
