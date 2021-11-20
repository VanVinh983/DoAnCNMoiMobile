package com.example.chatappcongnghemoi.models;

import java.util.ArrayList;
import java.util.List;

public class InitGif {
    public List<TypeGif> addTypeGif(){
        List<TypeGif> allType= new ArrayList<>();
        allType.add(new TypeGif(0,"Tất cả","all"));
        allType.add(new TypeGif(1,"Xin chào","hello"));
        allType.add(new TypeGif(2,"Cười","haha"));
        allType.add(new TypeGif(3,"Buồn","sad"));
        allType.add(new TypeGif(4,"Thích thú","like"));
        allType.add(new TypeGif(5,"Yêu thích","love"));
        allType.add(new TypeGif(6,"Xin lỗi","sorry"));
        allType.add(new TypeGif(7,"Cảm ơn","thanks"));
        allType.add(new TypeGif(8,"Thú cưng","pet"));
        allType.add(new TypeGif(9,"Vỗ tay","applause"));
        allType.add(new TypeGif(10,"Doraemon","doraemon"));
        allType.add(new TypeGif(11,"flower","flower"));
        allType.add(new TypeGif(12,"Quà","gift"));
        allType.add(new TypeGif(13,"Cầu xin","pray"));
        return allType;
    }
    public List<Gif> getGifsByType(String type){
        List<Gif> allGifs = addGif();
        List<Gif> results = new ArrayList<>();
        allGifs.forEach(gif -> {
            if(gif.getType().equals(type)){
                results.add(gif);
            }
        });
        return  results;
    }
    public List<Gif> addGif(){
        List<Gif> allGifs = new ArrayList<>();
        allGifs.add(new Gif(1,"https://media1.giphy.com/media/WpIPS0DWNpMm4kfMVr/giphy.gif?cid=ecf05e47sioa06tvqh7hou8zisnygwgwajt8d2u3vl2mqfje&rid=giphy.gif&ct=g","hello"));
        allGifs.add(new Gif(2,"https://media0.giphy.com/media/28GHfhGFWpFgsQB4wR/giphy.gif?cid=790b761138b5f077337acf7a682d6e1a00638caa58038990&rid=giphy.gif&ct=g","hello"));
        allGifs.add(new Gif(3,"https://media1.giphy.com/media/WS3i2y88foYpE584rI/giphy.gif?cid=790b7611e9bb32a1b7e765d0b5b1a50f329237e9048b8081&rid=giphy.gif&ct=g","hello"));
        allGifs.add(new Gif(4,"https://media1.giphy.com/media/Vbtc9VG51NtzT1Qnv1/giphy.gif?cid=790b76115836351d43a1a4b57f85d1be20b8935c265dd4af&rid=giphy.gif&ct=g","hello"));
        allGifs.add(new Gif(5,"https://media1.giphy.com/media/3o6Ztl7oraKm4ZJ9mw/giphy.gif?cid=790b76117f79a75f9690bd3f2ea75bedb313e4b7b104f5c0&rid=giphy.gif&ct=g","hello"));
        allGifs.add(new Gif(6,"https://media3.giphy.com/media/ASd0Ukj0y3qMM/giphy.gif?cid=790b7611e9dfd211ae654ba157b1e07d2fb89ed7b9a2c637&rid=giphy.gif&ct=g","hello"));
        allGifs.add(new Gif(7,"https://media2.giphy.com/media/11Wf3llSqbkgko/giphy.gif?cid=790b7611623fb209fd8cdde325e02ef04e3a2daace41c685&rid=giphy.gif&ct=g","hello"));
        allGifs.add(new Gif(8,"https://media1.giphy.com/media/IhLq8fGZw2SEE/giphy.gif?cid=790b76117e8d78370bab36000c36f91db1a7e895e95c7d84&rid=giphy.gif&ct=g","hello"));
        allGifs.add(new Gif(9,"https://media0.giphy.com/media/Y8ocCgwtdj29O/giphy.gif?cid=790b7611f04eb08fc2182323d6a7f8ffda92fa5258741655&rid=giphy.gif&ct=g","hello"));
        allGifs.add(new Gif(10,"https://media0.giphy.com/media/ihHEvEnww38KF2cK4F/giphy.gif?cid=790b761153eae803975fed5b5d49adf687f56334f464b58c&rid=giphy.gif&ct=g","hello"));
        allGifs.add(new Gif(11,"https://media3.giphy.com/media/GpyS1lJXJYupG/giphy.gif?cid=790b7611f1b5d22e0b454432073f481e7946e27fd1ccdf73&rid=giphy.gif&ct=g","haha"));
        allGifs.add(new Gif(12,"https://media.giphy.com/media/l8aCBaBuz5R6M/giphy.gif","haha"));
        allGifs.add(new Gif(13,"https://media.giphy.com/media/3ohhwxmNcPvwyRqYKI/giphy.gif","haha"));
        allGifs.add(new Gif(14,"https://media.giphy.com/media/OhrNfRrBxgz16/giphy.gif","haha"));
        allGifs.add(new Gif(15,"https://media.giphy.com/media/tMyCJmeXHBetq/giphy.gif","haha"));
        allGifs.add(new Gif(16,"https://media.giphy.com/media/98jU7NxuNSSZ2/giphy.gif","haha"));
        allGifs.add(new Gif(17,"https://media.giphy.com/media/I8nepxWwlEuqI/giphy.gif","haha"));
        allGifs.add(new Gif(18,"https://media.giphy.com/media/g8A1eJhTQ7Iic/giphy.gif","haha"));
        allGifs.add(new Gif(19,"https://media.giphy.com/media/gTNSX6N7vcKOY/giphy.gif","haha"));
        allGifs.add(new Gif(20,"https://media.giphy.com/media/KiaU2EUyxjQB2/giphy.gif","haha"));
        allGifs.add(new Gif(21,"https://media.giphy.com/media/BEob5qwFkSJ7G/giphy.gif","sad"));
        allGifs.add(new Gif(22,"https://media.giphy.com/media/d2lcHJTG5Tscg/giphy.gif","sad"));
        allGifs.add(new Gif(23,"https://media.giphy.com/media/9Y5BbDSkSTiY8/giphy.gif","sad"));
        allGifs.add(new Gif(24,"https://media.giphy.com/media/OPU6wzx8JrHna/giphy.gif","sad"));
        allGifs.add(new Gif(25,"https://media.giphy.com/media/6qFFgNgextP9u/giphy.gif","sad"));
        allGifs.add(new Gif(26,"https://media.giphy.com/media/1BXa2alBjrCXC/giphy.gif","sad"));
        allGifs.add(new Gif(27,"https://media.giphy.com/media/2WxWfiavndgcM/giphy.gif","sad"));
        allGifs.add(new Gif(28,"https://media.giphy.com/media/zvBuF2oYRErVS/giphy.gif","sad"));
        allGifs.add(new Gif(29,"https://media.giphy.com/media/26FPzgftlRfgwkEw0/giphy.gif","sad"));
        allGifs.add(new Gif(30,"https://media.giphy.com/media/O3JyUHiKqsviE/giphy.gif","sad"));
        allGifs.add(new Gif(31,"https://media.giphy.com/media/gPBKtKGk00TfD3D6mY/giphy.gif","like"));
        allGifs.add(new Gif(32,"https://media.giphy.com/media/14cilFdQzr8hG0/giphy.gif","like"));
        allGifs.add(new Gif(33,"https://media.giphy.com/media/Guccz4Oq87bncsm1j4/giphy.gif","like"));
        allGifs.add(new Gif(34,"https://media.giphy.com/media/jt2msevoEd9MYfnpjD/giphy.gif","like"));
        allGifs.add(new Gif(35,"https://media.giphy.com/media/V9ylxG8HD8cZW/giphy.gif","like"));
        allGifs.add(new Gif(36,"https://media.giphy.com/media/26FL8srd2MiCh2JmU/giphy.gif","like"));
        allGifs.add(new Gif(37,"https://media.giphy.com/media/xUA7aWtFh4RUUwpmJa/giphy.gif","like"));
        allGifs.add(new Gif(38,"https://media.giphy.com/media/1hqb8LwPS2xCNCpWH8/giphy.gif","love"));
        allGifs.add(new Gif(39,"https://media.giphy.com/media/bjkV301j0U2Mc4orf9/giphy.gif","love"));
        allGifs.add(new Gif(40,"https://media.giphy.com/media/2dQ3FMaMFccpi/giphy.gif","love"));
        allGifs.add(new Gif(41,"https://media.giphy.com/media/fnKgEwqbYM6YCGmTad/giphy.gif","love"));
        allGifs.add(new Gif(42,"https://media.giphy.com/media/5brPoXO6LC7AlmzasY/giphy.gif","love"));
        allGifs.add(new Gif(43,"https://media.giphy.com/media/YkYHSHS9NKZsNZnMJg/giphy.gif","love"));
        allGifs.add(new Gif(44,"https://media.giphy.com/media/l4pTdcifPZLpDjL1e/giphy.gif","love"));
        allGifs.add(new Gif(45,"https://media.giphy.com/media/26xBziMPJcCmBbpVm/giphy.gif","sorry"));
        allGifs.add(new Gif(46,"https://media.giphy.com/media/rvDtLCABDMaqY/giphy.gif","sorry"));
        allGifs.add(new Gif(47,"https://media.giphy.com/media/fxU6WfJ8eembhmZBC6/giphy.gif","sorry"));
        allGifs.add(new Gif(48,"https://media.giphy.com/media/3o7aD4XavHnL5UsWEE/giphy.gif","sorry"));
        allGifs.add(new Gif(49,"https://media.giphy.com/media/l0MYRE3WCmzXCzys8/giphy.gif","sorry"));
        allGifs.add(new Gif(50,"https://media.giphy.com/media/uWlpPGquhGZNFzY90z/giphy.gif","thanks"));
        allGifs.add(new Gif(51,"https://media.giphy.com/media/BYoRqTmcgzHcL9TCy1/giphy.gif","thanks"));
        allGifs.add(new Gif(52,"https://media.giphy.com/media/l3q2wJsC23ikJg9xe/giphy.gif","thanks"));
        allGifs.add(new Gif(53,"https://media.giphy.com/media/6tHy8UAbv3zgs/giphy.gif","thanks"));
        allGifs.add(new Gif(54,"https://media.giphy.com/media/xuK0uWYApXbE6mMODf/giphy.gif","thanks"));
        allGifs.add(new Gif(55,"https://media.giphy.com/media/kBSLuVTTMgGuSLO78G/giphy.gif","pet"));
        allGifs.add(new Gif(56,"https://media.giphy.com/media/jivGITd768psP80B2i/giphy.gif","pet"));
        allGifs.add(new Gif(57,"https://media.giphy.com/media/ASsGSJEh0a63u/giphy.gif","pet"));
        allGifs.add(new Gif(58,"https://media.giphy.com/media/Dcf2hNSaAiLV6/giphy.gif","pet"));
        allGifs.add(new Gif(59,"https://media.giphy.com/media/ZXlDOOsfV0a8U/giphy.gif","pet"));
        allGifs.add(new Gif(60,"https://media.giphy.com/media/fyx8vjZc2ZvoY/giphy.gif","pet"));
        allGifs.add(new Gif(61,"https://media.giphy.com/media/ZU9QbQtuI4Xcc/giphy.gif","applause"));
        allGifs.add(new Gif(62,"https://media.giphy.com/media/4PXUYM1bXS3lRXO7lX/giphy.gif","applause"));
        allGifs.add(new Gif(63,"https://media.giphy.com/media/fnK0jeA8vIh2QLq3IZ/giphy.gif","applause"));
        allGifs.add(new Gif(64,"https://media.giphy.com/media/tBb19fgUbbUVCNji0iA/giphy.gif","applause"));
        allGifs.add(new Gif(65,"https://media.giphy.com/media/l3q2XhfQ8oCkm1Ts4/giphy.gif","applause"));
        allGifs.add(new Gif(66,"https://media.giphy.com/media/3o72FcJmLzIdYJdmDe/giphy.gif","applause"));
        allGifs.add(new Gif(67,"https://media.giphy.com/media/mGK1g88HZRa2FlKGbz/giphy.gif","applause"));
        allGifs.add(new Gif(68,"https://media.giphy.com/media/kBZBlLVlfECvOQAVno/giphy.gif","applause"));
        allGifs.add(new Gif(69,"https://media.giphy.com/media/l0MYJnJQ4EiYLxvQ4/giphy.gif","applause"));
        allGifs.add(new Gif(70,"https://media.giphy.com/media/26FxCOdhlvEQXbeH6/giphy.gif","applause"));
        allGifs.add(new Gif(71,"https://media.giphy.com/media/TXS5vPPSFbJvi/giphy.gif","doraemon"));
        allGifs.add(new Gif(72,"https://media.giphy.com/media/noKPfoiakFXsk/giphy.gif","doraemon"));
        allGifs.add(new Gif(73,"https://media.giphy.com/media/l4EoZ1rJtDfypcna8/giphy.gif","doraemon"));
        allGifs.add(new Gif(74,"https://media.giphy.com/media/RGXSAqVn89E3ZlzWGW/giphy.gif","doraemon"));
        allGifs.add(new Gif(75,"https://media.giphy.com/media/26Ff9SXbIDm5m0bW8/giphy.gif","doraemon"));
        allGifs.add(new Gif(76,"https://media.giphy.com/media/7EcJUK3l9YB2w/giphy.gif","flower"));
        allGifs.add(new Gif(77,"https://media.giphy.com/media/l0MYAs5E2oIDCq9So/giphy.gif","flower"));
        allGifs.add(new Gif(78,"https://media.giphy.com/media/3ohze05oAwDp45QsYo/giphy.gif","flower"));
        allGifs.add(new Gif(79,"https://media.giphy.com/media/PkHbMg4d0yZ7a/giphy.gif","flower"));
        allGifs.add(new Gif(80,"https://media.giphy.com/media/26gN2pxF0yTSeZFqo/giphy.gif","flower"));
        allGifs.add(new Gif(81,"https://media.giphy.com/media/XXadOmdQQVQyZfVnb8/giphy.gif","gift"));
        allGifs.add(new Gif(82,"https://media.giphy.com/media/jox7UHMmZ6FRBFHwLc/giphy.gif","gift"));
        allGifs.add(new Gif(83,"https://media.giphy.com/media/9JnU6uRRblKM3hGzu3/giphy.gif","gift"));
        allGifs.add(new Gif(84,"https://media.giphy.com/media/xUOxf5webfaPR4nZ5K/giphy.gif","gift"));
        allGifs.add(new Gif(85,"https://media.giphy.com/media/5PhYotn1t5s0M10Aly/giphy.gif","gift"));
        allGifs.add(new Gif(86,"https://media.giphy.com/media/3orieTfp1MeFLiBQR2/giphy.gif","pray"));
        allGifs.add(new Gif(87,"https://media.giphy.com/media/l1J9ETWjKTdN7FZOE/giphy.gif","pray"));
        allGifs.add(new Gif(88,"https://media.giphy.com/media/jLUtZxEUjQa3e/giphy.gif","pray"));
        allGifs.add(new Gif(89,"https://media.giphy.com/media/3orieLeZL5kyNqiLfO/giphy.gif","pray"));
        allGifs.add(new Gif(90,"https://media.giphy.com/media/IoXVrbzUIuvTy/giphy.gif","pray"));
        return allGifs;
    }
}
