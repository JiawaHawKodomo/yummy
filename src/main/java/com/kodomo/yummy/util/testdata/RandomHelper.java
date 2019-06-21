package com.kodomo.yummy.util.testdata;

import com.kodomo.yummy.util.Utility;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Shuaiyu Yao
 * @create 2019-06-04 13:05
 */
@Component
public class RandomHelper {

    private List<String> emailDomainNames = new ArrayList<>(Arrays.asList("qq.com", "163.com", "125.com",
            "google.com", "smail.nju.edu.cn"));

    private List<String> offeringNames = new ArrayList<>(Arrays.asList("瓜丝儿", "山鸡丁儿", "拌海蜇", "龙须菜", "炝冬笋", "玉兰片", "浇鸳鸯", "烧鱼头", "烧槟子", "烧百合", "炸豆腐", "炸面筋", "糖熘 儿", "拔丝山药", "糖焖莲子", "酿山药", "杏仁酪", "小炒螃蟹", "氽大甲", "什锦葛仙米", "蛤蟆鱼", "扒带鱼", "海鲫鱼", "黄花鱼", "扒海参", "扒燕窝", "扒鸡腿儿", "扒鸡块儿", "扒肉", "扒面筋", "扒三样儿", "油泼肉", "酱泼肉", "炒虾黄儿", "熘蟹黄儿", "炒子蟹", "佛手海参", "炒芡子米", "奶汤", "翅子汤", "三丝汤", "熏斑鸠", "卤斑鸠", "海白米", "烩腰丁儿", "火烧茨菰", "炸鹿尾儿", "焖鱼头", "拌皮渣儿", "氽肥肠儿", "清拌粉皮儿", "木须菜", "烹丁香", "烹大肉", "烹白肉", "麻辣野鸡", "咸肉丝儿", "白肉丝儿", "荸荠", "一品锅", "素炝春不老", "清焖莲子", "酸黄菜", "烧萝卜", "烩银耳", "炒银枝儿", "八宝榛子酱", "黄鱼锅子", " 白菜锅子", "什锦锅子", "汤圆子锅", "菊花锅子", "煮饽饽锅子", "肉丁辣酱", "炒肉丝儿", "炒肉片", "烩酸菜", "烩白菜", "烩豌豆", "焖扁豆", "氽毛豆"));

    private List<String> customerNames = new ArrayList<>(Arrays.asList("希思莲", "闽乐悦", "前升", "迟牧歌", "葛和泰", "化理", "寇荣", "马如", "泣羽彤", "摩嘉悦", "谢含云", "卿晶辉", "夕古香", "过星星", "汉灵波", "贵如柏", "汝寻桃", "涂秀丽", "巢子童", "文傲松", "俞以柳", "左丘玥", "玉清昶", "荆昂然", "由安娜", "允绮南", "温绮兰", "牛骞北", "天飞燕", "京希慕", "叶理群", "古顺", "才含灵", "寻青香", "亥起运", "泰萱彤", "茅立", "哈奇逸", "蔡秋柔", "东冷亦", "止曜文", "折兴朝", "盍雪卉", "阎艳蕙", "习蕴美", "邴书文", "铁嘉歆", "楚运升", "胡略", "敬春芳", "俎凌春", "纳奕叶", "盘曜栋", "齐迎丝", "柏宁", "答笑晴", "睦青香", "祈彗", "仪高阳", "乌亦玉", "年勇锐", "蒲晴雪", "钞静", "魏恨之", "智俊哲", "梁云岚", "鄂醉巧", "区香岚", "詹淳雅", "终澹", "逯剑", "介元灵", "全贤", "保微月", "巫月灵", "谈晴岚", "章向雪", "永敏叡", "宫长钰", "柳淑", "郦小蕊", "陶昊嘉", "逢弘雅", "嵇清雅", "有成弘", "贲同光", "游浩言", "路成荫", "生心诺", "瓮咏歌", "童穰", "卞安春", "厉秀敏", "肇乐家", "酒以", "公西鸿畴", "和欣嘉", "濯锦曦", "苍恨蝶", "清萍韵", "尤小春", "律囡囡", "罗幼菱", "容奇希", "建语心", "候白梅", "弥歌韵", "泥祯", "莱以轩", "亓云亭", "仝觅山", "赏晓凡", "畅越", "郁隽", "喜奇致", "蹉芳芳", "析向文", "西门语", "沐绿海", "史静逸", "步仁", "赖朝旭", "应奥婷", "士孟", "侍凌春", "戴妍和", "司空乐安", "伟俊贤", "农翠琴", "武凯康", "僪芷文", "唐涵阳", "郑俏", "綦杰", "汤绣", "禚清芬", "尹巍奕", "类思松", "纵和煦", "法醉蝶", "宇文欣悦", "表若英", "拓跋星辰", "南凝心", "佟佳晓曼", "俟掣", "申屠令怡", "阮康成", "项涉", "戈晨璐", "亓官孟阳", "同承安", "瞿痴柏", "毋和顺", "利紫南", "用凡阳", "来修贤", "谬幻玉", "乜乐蓉", "昝巧云", "劳学民", "柯震博", "傅音景", "焦寻双", "浦骊红", "悟尔雅", "袁曼岚", "井水风", "朋静婉", "辜冷雪", "邛如仪", "归鸿运", "府淑贤", "焉朗丽", "侯旎旎", "速修真", "沈幻竹", "绪冰双", "乔海雪", "阳骏祥", "检宛亦", "融锐志", "段逸秀", "依巧曼", "闪安露", "九晓丝", "肖雪松", "真烨", "秦清华", "呼延新霁", "念平乐", "沙岚岚", "丙香彤", "回骊蓉", "索浩大", "闳曜", "随静秀", "哀顺美", "掌梦桃", "弘端雅", "夫从筠", "蒯冷荷", "左秀隽", "姓晓兰", "满书桃", "庄芳蕙", "庹春蕾", "怀雁丝", "裴洛妃", "时雅彤", "校阳嘉", "嬴芷容", "千润", "隐玮奇", "拱永安", "戎德明", "林智志", "姜觅柔", "吴愉婉", "荣晓山", "顿夏烟", "巫马仙媛", "澹台采莲", "赫连南露", "万海逸", "玄羡", "泉梦菲", "奉炎彬", "谯嘉怡", "银清妙", "邓孤丹", "公良俊晖", "道彦露", "毓又琴", "粘禹", "常香旋", "续瀚钰", "富新荣", "节幼丝", "伏雪儿", "宛新冬", "隽凯复", "长孙初蝶", "范姜采", "聊和", "倪问", "闻明轩", "己德元", "乙香彤", "山长逸", "欧阳亦云", "皇成双", "丛春翠", "冠南晴", "鄢玲玲", "霍义", "藤凌", "咸谷菱", "樊鹏翼", "愚白萱", "恽子怡", "缪鸿卓", "蓟泽雨", "卢木", "针颐", "韶海瑶", "阿景辉", "莘三诗", "那拉恨风", "五洁静", "宜平凡", "翁叶", "圭幻露", "寿谷兰", "秋盼晴", "艾宣", "赫之卉", "向谷秋", "羽睿好", "红雪萍", "公羊广君", "燕映雪", "符幼霜", "琴博", "德博涛", "寸白桃", "都坚诚", "相秋阳", "谭玮琪", "禾瀚昂", "所千亦", "杞雅容", "睢隽巧", "行吹", "聂州", "兴念露", "乐静晨", "笪晨星", "撒浩瀚", "池梦菲", "司徒韫", "字梦", "帛谷兰", "似骥", "星凝丹", "官玲珑", "祭寄真", "福寄文", "万俟振荣", "本琪", "堂雁菡", "杭沙羽", "示山灵", "莫乔", "祁丽姝", "宣新蕾", "彤凌青", "岳映冬", "烟忆文", "上官锐达", "禽曼辞", "公冶若雁", "幸白翠", "镜凝竹", "仇英光", "养白萱", "庞涵易", "米忆文", "褒寻芹", "鱼绮美", "苟小枫", "市夏兰", "柴名", "鞠冬亦", "刘孟", "称晨辰", "牢鸿禧", "佴丽君", "受如云", "臧建修", "郭昊苍", "凭怡", "吉春娇", "淳于之桃", "于桐", "鲜琼", "洪书雁", "许凝静", "党豆", "完颜慧秀", "夏和平", "栾春翠", "房幻天", "果庄雅", "纪梦寒", "宇欣艳", "薄语诗", "驹秀颖", "晋谷槐", "覃安青", "疏夏", "鹿玲", "载寄蕾", "巩璧", "青思松", "龙大", "说吉星", "蒙迎夏", "英念之", "良馥芬"));

    private List<String> restaurantNames = new ArrayList<>(Arrays.asList("莲春园", "大鸭梨", "啃壹锅", "万龙洲", "莫劳龙玺", "福楼法", "九花山", "华天大地", "莫斯科", "鹅和鸭", "起士林", "兰特伯爵", "全聚德", "便宜坊", "利群", "金百万", "长安壹号", "鸭王", "白魁", "护国寺", "盆盆鲜", "圆明清", "孔府宴", "福人居", "红湖", "俏江南", "沸腾久久", "顺福华", "花家怡园", "桃源酒家", "楚香阁", "避风塘茶餐厅", "萃华楼", "鼎鼎香涮肉坊", "东方怡园", "彩云楼", "大江南俱乐部-杭州美食餐厅", "长寿楼酒楼", "楚湘斋", "川佛餐厅", "不见不散餐厅", "大连湾海鲜城", "鼎泰珍美食世界"));

    private List<String> typeNames = new ArrayList<>(Arrays.asList("热销推荐", "今日折扣", "炒菜", "凉菜", "凉拌菜", "饮品", "店家推荐", "煎炸类", "甜品类", "蒸煮类", "零食"));

    String randomTelephone() {
        StringBuilder sb = new StringBuilder();
        sb.append('1');
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            sb.append((char) ('0' + random.nextInt(10)));
        }
        return sb.toString();
    }

    String randomIndex(int bound) {
        return String.valueOf(randomInt(bound));
    }

    int randomInt(int bound) {
        Random random = new Random();
        return random.nextInt(bound);
    }

    boolean randomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }

    String randomEmail() {
        String prefix = Utility.getSHA256Str(String.valueOf(System.nanoTime())).substring(0, 8);
        return prefix + "@" + emailDomainNames.get(randomInt(emailDomainNames.size()));
    }

    Date randomTime(Date from, Date to) {
        Random random = new Random();
        Date date = new Date();
        date.setTime(from.getTime() + (long) ((to.getTime() - from.getTime()) * random.nextDouble()));
        return date;
    }

    <C extends Collection<K>, K> C randomCollection(C a) {
        Collection<K> collection = null;
        if (a instanceof Set) {
            collection = new HashSet<>();
        } else if (a instanceof List) {
            collection = new ArrayList<>();
        }

        for (K k : a) {
            if (randomBoolean()) {
                collection.add(k);
            }
        }
        return (C) collection;
    }

    <K> K randomValue(Collection<K> collection) {
        return new ArrayList<>(collection).get(new Random().nextInt(collection.size()));
    }

    <K> List<K> randomSubList(Collection<K> collection, int num) {
        List<K> list = new ArrayList<>(collection);
        List<K> result = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            int index = new Random().nextInt(list.size());
            result.add(list.get(index));
            list.remove(index);
        }
        return result;
    }

    String randomOfferingName() {
        return randomValue(offeringNames);
    }

    String randomCustomerName() {
        return randomValue(customerNames);
    }

    String randomRestaurantName() {
        return randomValue(restaurantNames);
    }

    List<String> randomOfferingTypeNames(int num) {
        return randomSubList(typeNames, num);
    }
}
