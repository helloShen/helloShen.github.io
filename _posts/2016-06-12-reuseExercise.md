---
layout: post
title: "Code Refactoring - War of Clan"
date: 2016-06-12 12:44:46
author: "Wei SHEN"
categories: ["java","design pattern"]
tags: ["inheritance","composition","factory pattern"]
---

### 前言
从考法语的状态回来，整个过程虽说很煎熬很虐心，但说实在的法语水平真的是有长进，我一直是相信煎熬对人性是一种试炼的。这么多年一直以为自己已经不用练法语了，但显然一门语言是没有止境的。练口语的过程中，认识了不少本地的魁瓜，竟然有人工作之余的兴趣爱好就是帮助新移民提高法语水平你能信？我一直不是一个成功主义者，这些默默无闻的人，是不是很像遍地的野花，其实也很可爱呢？但在一些人人追逐成功的地方，你很难找到这样的人。总之，加拿大这个国家还是很可爱的，每个故事都会有一只呆萌的胖子，很明显加拿大就是我们这个世界里的那只胖子。

回到编程，之前Think in Java看到组合，继承，代理。中断了这么久，继续之前，周末先做个小练习，重温一下书里说的一些编程风格。最初的动机只是想做一个最简单的"人类-家族-世界"的模型。但因为之前喝了KK的毒奶《失控》，看完我彻底被洗脑了呀。上帝模式下，想象在一片远古大陆上，各种拥有不同特长能力的种族，相互竞争，繁衍生息，最后会不会有某一种族统治这片大陆？如果有，那到底什么样的特长是最强的？能帮助一个种族最后生存下来？带着这个傻X的想法我就写了这个叫《氏族战争》的小游戏。

稍微剧透一下，结局略狗血，最后的胜利者竟然是这货！！！完全刷新了朕的三观。在一个看颜值的世界，高颜值的玫瑰家族差不多垫底，简直不能忍啊！所以我在想，什么时候能再写一个《氏族战争2》，能不能不要让上面这货赢？

### 游戏规则
这片大陆的名字叫艾泽拉斯。

每年的春季，大陆上的未婚妹纸都会向她们周围最帅的未婚小鲜肉表白。但帅哥的心上人是他社交圈内最美那个妹纸，如果正巧就是表白的那妹纸的话，他们就会相爱，并结婚生子。人均生育率为2。婚后妻子加入丈夫的氏族，孩子也跟老爸姓，遗传老爸的能力。

但世界上还有一种好东西叫钱。妹子也会仰慕周围最有钱的富豪，如果这妹子正好是这位富豪的心上人的话，她就会被包养成为情妇（等同于结婚）。

婚后妻子有可能出轨，具体情况根据夫妇俩的忠诚度以及小三的诱惑值决定。妻子出轨以后怀的孩子都是小三的，跟小三姓。

每年夏天，求偶失败的男孩会到周边的地方重新求偶，直到成功或者挂掉为止。

人的颜值会随着年龄的增长衰减。

大陆上有七个奇怪的种族：
* <img src="/uploads/clanWar/rose.png" width=100 height=100/>**玫瑰家族(Rose)**:	高颜值。家族成员的平均颜值80分。其他家族平均颜值只有50分。

* <img src="/uploads/clanWar/banker.png" width=100 height=100/>**银行家家族(Banker)**:	有经济头脑，更会赚钱。银行家人均年收入100刀。其他家族年收入只有50刀。

* <img src="/uploads/clanWar/ranger.png" width=100 height=100/>**巡游者家族(Ranger)**:	爱好冒险和旅游。巡游者每年在地图上平均移动70公里，其他家族只移动30公里。

* <img src="/uploads/clanWar/skaven.png" width=100 height=100/>**鼠人族(Skaven)**:	更强的生育能力。鼠人人均生育率=2.1。其他家族=2.0。

* <img src="/uploads/clanWar/playboy.png" width=100 height=100/>**花花公子家族(Playboy)**:	对婚姻不忠诚，专门勾引有妇之夫（别人老婆怀的是他的孩子！）。其他家族婚姻忠诚度50，诱惑能力20。花花公子忠诚度45，诱惑能力25。

* <img src="/uploads/clanWar/barbarian.png" width=100 height=100/>**兽人族(Barbarian)**:	对配偶颜值没有任何要求，再丑都无所谓。其他家族男性成员只接受他们社交范围内最漂亮女生的表白，兽人会接受任（饥）何（不）妹（择）纸（食）的表白。

* <img src="/uploads/clanWar/nobody.png" width=100 height=100/>**白板族(Nobody)**:	苦逼的白板，没有任何特殊能力，人艰不拆。

300年后人口最多的种族成为世界的霸主！最后的胜利者只有一个，会是哪个种族呢？

### 代码设计
设计类的基本原则：
* 最小化代码冗余
* 松耦合模块化，降低维护难度
* 运用虚拟工厂设计模式，对用户隐藏不同氏族成员实例化的细节。
* 提高扩展性，尤其是将来扩展更多的种族，甚至允许玩家自己设计扩展种族参与乱斗。
* 集中游戏的参数，方便初始化游戏
* 实践Think in Java中谨慎的访问权限风格，私有参数，私有构造函数，公开方法，包内权限类。

#### 人类（Human.java）
![result](/images/clanWar/human.jpg)
是最基本的游戏单位。包含每个个体的基本参数和行为方法。最主要的有恋爱系统girlFlirt()，loveMe()，出轨系统havingAffair()，寿命系统randomDeath()，人物移动系统move()等等。另外还提供两种工厂方法接口：
* 开放性的万能工厂createNewMan()，方便其他类灵活地定制人类实例。
* 封闭的特殊工厂babyBirth()，婴儿所有参数根据其父母参数随机产生，用户无法控制参数。

#### 氏族（Clan.java）
![result](/images/clanWar/clan_1.jpg)
是人类的虚拟工厂(abstract factory)类。主要包含生产标准人类的各种必要参数，以及工厂方法ClanMemberFactory()，专门用来生产人类的实例。chargeClan()方法调用ClanMemberFactory()方法批量生产氏族成员。氏族类作为基类提供了一个白板人类的标准模板(白板族的工厂类Nobody.java就是直接继承Clan.java，没有做任何改动)，等待其子类具体各个不同的氏族进一步异化人类实例。

#### 七大种族
![result](/images/clanWar/world.png)
（ClanRose.java, ClanBanker.java, ClanRanger.java, ClanSkaven.java, ClanPlayboy.java, ClanBarbarian.java, ClanNobody.java）继承Clan.java。但分别包含每个氏族的特殊参数。Clan只是个虚拟工厂，并不直接生产人类实例。而是将实例化的过程推迟到了其子类各个具体的氏族来完成。生产出人类实例自带此氏族特殊能力。但因为多态性，对用户而言，不需要关心底层哪怕是100个氏族具体是什么，调用的始终是Clan类的chargeClan()方法，但多态性确保每次都会调用某个种族特有的chargeClan()方法。
```java
Clan theClan=Clan.createTheClan(clanName);
theClan.chargeClan(memberNum,world);
```
虚拟工厂这样与底层无关，低依赖，松耦合的特性，非常有利于代码的扩展，哪怕将来氏族数量从7个扩展到100个，也完全不用修改客户端的代码。

#### 大陆（TheWorld.java）
包含世界的一些基本设定参数，以及两个人类的容器：ArrayList<Human> humanList和ArrayList<Human> heaven，分别包含所有活着的和死去的人类。TheWorld类最主要的两个方法：passOneYear()和spring()，来推动历史的发展，人类的生老病死。

#### 氏族战争（WarOfClan.java）
是游戏的主程序。runTheGame()方法首先创建一个TheWorld实例，然后调用Clan工厂在世界中创造人类实例。然后再调用世界的passOneYear()方法，完成推演。其中，还包括一个统计方法collectResult()用来统计每一年世界人口的分布。

#### 图形板（GraphPanel.java）
负责把统计结果，以曲线图的形式直观地显示出来。利用了awt和swing库。



### 实验结果
实验初始设置如下：
* 世界大小：1000*1000
* 初始人口密度：每个种族1000人
* 平均生育率：2

![result](/images/clanWar/result_1.png)

没想到人类在300年不到就灭亡了，这里平均生育率是个很敏感的值，一旦超过2，人口就会爆炸，低于2就会以非常快的速度灭亡。

最后的胜利者是花花公子家族！抢别人老婆的技能变相提高了生育率，果然生育率才是王道！但其实我只是稍微把他的诱惑值提高了5点。普通家族平均50点，他才55点，也不知道这区区5点为什么效果这么好。

第二名的优势家族竟然是兽族，看来在这个世界，不能太挑，再丑的妹子也是妹子啊！现在剩男剩女这么多，天天非诚勿扰，大妈满大街替儿子约会，就是要求太高，太挑了嘛。

最出乎意料的是高颜值的玫瑰家族，竟然垫底了！从结果上看，也就刚开始的几年他稍微领先，他优先把最漂亮的妹子选走了，但后来其他种族也都和颜值一般的姑娘结婚了，所以其实高颜值族没什么优势。但娶高颜值老婆却有一个明显的副作用：潘金莲！玫瑰家族净帮别人养娃了！同样的悲剧也发生在银行家和巡游者的身上，这证明在小三面前颜值，金钱，知识，努力，一切都是浮云！

最后一个比较特殊的鼠人！其实他才是隐藏的大Boss。刚开始天真的我是把他的生育率设成3的。然后我就发现他的人口爆炸了！100年之后，电脑都跑不动了！！！哪怕我只是稍微把鼠人的生育率调到2.5，也就是50%的雄性鼠人繁殖3个后代，50%还是繁殖2个后代。在去掉花花公子家族抢老婆的效果以后，150年后的霸主就是鼠人族！

![result](/images/clanWar/result_2.png)

当然如果再加上花花公子大Boss的话，结果就变成了这样，鼠人也只有替人打工的命。小三技能果然逆天。

![result](/images/clanWar/result_3.png)

所以这个实验告诉我们，优生优育什么的都是鬼扯！培养两个哈佛法学院博士不如实实在在生3个娃。现代社会推行普世价值，每个人生存的权利都被充分地尊重，高智商，高颜值已经不能为个人赢得生存的优势，反而降低了他们的生殖意愿。广大的劳动人民不要气馁，社会精英们其实都在为你们的后代打工。ISIS其实也不用再搞什么恐怖袭击，伤害到花花草草多不好。只要接着一窝一窝地生（我一个伊斯兰国家的兄弟，老妈生了17个娃），再来几波难民潮，欧洲直接就是你们的了。


### 代码

#### 人类（Human.java）

```java
/**
 *  The unit in the WarOfClan
 *  Human class provide a simple model of human. (features and constructor)
 *  @author wei.shen@iro.umontreal.ca
 *  @version 1.0
 */
package com.ciaoshen.thinkinjava.clanWar;
import java.util.*;
import java.lang.Math.*;

/**
 *  Only package access to this class
 *  players do not need to know the details
 */
class Human {

    /**
     *  public access to the private fields
     *  we cannot change these parameter, but only to show their value
     */
    public String getName(){return this.name;}
    public int getAge(){return this.age;}
    public SEX getSex(){return this.sex;}
    public String getClanName(){return this.clanName;}
    public Human getSpouse(){return this.spouse;}
    public int[] getPosition(){return this.position;}
    public int getBeauty(){return this.beauty;}
    public int getMoney(){return this.money;}
    public boolean isAlive(){return this.alive;}
    public int getAvgLife(){return this.avgLife;}
    public int isBornAt(){return this.bornAt;}
    public int isDeadAt(){return this.deadAt;}
    public int getRange(){return this.range;}
    public int getView(){return this.view;}
    public int getMonyAbility(){return this.moneyAbility;}
    public int getBabyNum(){return this.babyNum;}
    public int getBabyAbility(){return this.babyAbility;}
    public String getChildFather(){return this.childFather;}
    public int geLoyalty(){return this.loyalty;}
    public int getFlirt(){return this.flirt;}
    //check spouse before marriage
    public boolean hasSpouse(){
        if(this.spouse==null){
            return false;
        } else {
            return true;
        }
    }

    /**
     *  some special accesses to adjust some arguments
     */
    public void addAge(int age){this.age+=age;}
    public void toDeath(){this.alive=false;}
    public void earnMoney(int earn){this.money+=earn;}
    public void lostMoney(int lost){this.money-=lost;}
    public void bornAt(int year){this.bornAt=year;}
    public void deadAt(int year){this.deadAt=year;}
    public void changeView(int view){this.view=view;}
    //human beauty system
    public void growth(){
        this.beauty+=1;
        if(this.beauty>100){
            this.beauty=100;
        }
    }
    public void fading(){
        this.beauty-=2;
        if(this.beauty<0){
            this.beauty=0;
        }
    }



    /**
     *  Two different Human constructors
     *  based on the private universal constructor
     */

    //A closed proxy for a particular purpose
    //mother give birth with random sex, random name
    //baby inherit mothers avgLife and fathers clanName, beauty, range and moneyAbility
    public Human babyBirth(TheWorld world){
        Random myRander=new Random();
        int x=myRander.nextInt(world.getWidth());
        int y=myRander.nextInt(world.getHeight());
        int[] babyPosition={x,y};
        int babyBeauty=this.getSpouse().beauty;
        //int babyBeauty=(this.beauty+this.getSpouse().beauty)/2;
        int babyRange=this.getSpouse().range;
        int babyView=this.getSpouse().view;
        int babyMoneyAbility=this.getSpouse().moneyAbility;
        int babyBabyAbility=this.getSpouse().babyAbility;
        Human newBaby = new Human(Name.randomName(), this.childFather, 0, SEX.randomSex(), babyPosition, babyBeauty, 0, true, this.avgLife, world.getYear(), 0, babyRange, babyView, babyMoneyAbility, 0, babyBabyAbility, null, this.spouse.loyalty, this.spouse.flirt);
        this.babyNum-=1;
        return newBaby;
    }

    //A highly flexible proxy of private construcor
    //each clan generate their new members with their features
    public static Human createNewMan(String inputName, String inputClanName, int inputAge, SEX inputSex, int[] inputPosition, int inputBeauty, int inputMoney, boolean inputAlive, int inputAvgLife, int inputBorn, int inputDeath, int inputRange, int inputView, int inputAbility, int inputBabyNum, int inputBabyAbility, String inputChildFather, int inputLoyalty, int inputFlirt){
        return new Human(inputName, inputClanName, inputAge, inputSex, inputPosition, inputBeauty, inputMoney, inputAlive, inputAvgLife, inputBorn, inputDeath, inputRange, inputView, inputAbility, inputBabyNum, inputBabyAbility, inputChildFather, inputLoyalty, inputFlirt);
    }



    /**
     *  lifetime system
     *  with different mortality rate
     *  older people has more chance to die
     */
    public boolean randomDeath(){
        boolean toDeath=false;
        Random deathRander=new Random();
        if(age>(avgLife-20)){
            int deathSeed=deathRander.nextInt(200);
            if(deathSeed==0){
                toDeath=true;
            }
        }
        if(age>(avgLife-10)){
            int deathSeed=deathRander.nextInt(200);
            if(deathSeed==0){
                toDeath=true;
            }
        }
        if(age>(avgLife)){
            int deathSeed=deathRander.nextInt(100);
            if(deathSeed==0){
                toDeath=true;
            }
        }
        if(age>(avgLife+10)){
            int deathSeed=deathRander.nextInt(50);
            if(deathSeed==0){
                toDeath=true;
            }
        }
        if(age>(avgLife+20)){
            int deathSeed=deathRander.nextInt(35);
            if(deathSeed==0){
                toDeath=true;
            }
        }
        if(age>(avgLife+30)){
            int deathSeed=deathRander.nextInt(15);
            if(deathSeed==0){
                toDeath=true;
            }
        }
        return toDeath;
    }


    /**
     *  flirt system
     */

    public void girlFlirt(TheWorld world){
        Human beautifulOne=null;
        Human richOne=null;
        //girl chose the most beautiful one and the richest one in her range.
        for(Iterator<Human> it=world.getHumanList().iterator();it.hasNext();){
            Human hm=it.next();
            //single man, adult, in her range
            if(hm.sex.equals(SEX.MALE) && hm.age>=18 && this.inTheView(hm) && !hm.hasSpouse()){
                //chose the beautiful one
                if(true){
                    if(beautifulOne!=null && hm.getBeauty()>beautifulOne.getBeauty()){
                        beautifulOne=hm;
                    } else if(beautifulOne==null){
                        beautifulOne=hm;
                    }
                }
                //chose the rich one
                if(richOne!=null && hm.getMoney()>richOne.getMoney()){
                    richOne=hm;
                } else if(richOne==null){
                    richOne=hm;
                }
            }
        }
        //System.out.println("@@@"+beauifulOne);
        //System.out.println("$$$"+richOne);
        //girl prefert the richest one than the beautiful one
        if(richOne!=null && this.payMe(richOne,world)){
            richOne.marriage(this);
        } else if(beautifulOne!=null && this.loveMe(beautifulOne,world)){
            beautifulOne.marriage(this);
        }
    }

    //the boy answer the girl
    public boolean payMe(Human boy, TheWorld world){
        if(this.loveMe(boy,world)){
            //how much
            int price=this.beauty*10;
            //if the boy has enough money
            if(boy.money>=price){
                boy.money-=price;
                this.money+=price;
                return true;
            }
        }
        return false;
    }

    //he will chose the most beautiful girl in his range whose beauty is lower than him
    public boolean loveMe(Human boy, TheWorld world){
        int bestBeauty=0;
        for(Iterator<Human> it=world.getHumanList().iterator();it.hasNext();){
            Human hf=it.next();
            if(hf.sex.equals(SEX.FEMALE) && boy.inTheView(hf) && hf.beauty<boy.beauty){
                if(hf.beauty>bestBeauty){
                    bestBeauty=hf.beauty;
                }
            }
        }
        if(bestBeauty<=this.beauty){
            return true;
        } else{
            return false;
        }
    }

    //marriaged women looking for playboys
    public void havingAffair(TheWorld world){
        Human lover=null;
        for(Iterator<Human> it=world.getHumanList().iterator();it.hasNext();){
            Human hm=it.next();
            if(hm.sex.equals(SEX.MALE) && hm.age>=18 && this.inTheView(hm)){
                if(lover==null){
                    lover=hm;
                } else if(hm.flirt>lover.flirt){
                    lover=hm;
                }
            }
        }
        //more beaufiful the women are, easier to betray
        int loyalty=this.loyalty+this.spouse.loyalty-this.beauty;
        if(lover!=null && lover.flirt>loyalty){
            this.childFather=lover.clanName;
            //System.out.println(this.name+" flirted!!!!");
        }
    }

    public boolean inTheView(Human another){
        double distance=Math.sqrt(Math.pow(this.position[0]-another.position[0],2)+Math.pow(this.position[1]-another.position[1],2));
        if(distance<=(double)this.view){
            return true;
        } else {
            return false;
        }
    }

    //marriage
    public void marriage(Human girl){
        if(!this.hasSpouse() && !girl.hasSpouse()){
            this.spouse=girl;
            girl.spouse=this;
            girl.clanName=this.clanName;
            girl.position=this.position;
            girl.childFather=this.clanName;
            //the baby number depends on the father
            if(this.age<50){
                Random babyRander=new Random();
                int babyIndice=babyRander.nextInt(2);
                switch(babyIndice){
                    case 0:
                        Math.max(0,girl.babyNum=this.babyAbility-1);
                        break;
                    case 1:
                        girl.babyNum=this.babyAbility+1; break;
                }
            }
        }else{
            System.out.println("Warning! They have already spouse, they can not marry each other!");
        }
    }

    //single MALE random move in the world (>15 years old)
    public void move(TheWorld world){
        if(this.sex.equals(SEX.MALE) && this.age>15 && !this.hasSpouse()){
            Random myRander=new Random();
            int distance=myRander.nextInt(this.range+1);
            int direction=myRander.nextInt(4);
            switch(direction){
                case 0:
                    this.position[0]+=distance;
                    this.position[1]+=distance;
                    if(this.position[0]>world.getWidth()){
                        this.position[0]=this.position[0]%world.getWidth();
                    }
                    if(this.position[1]>world.getHeight()){
                        this.position[1]=this.position[1]%world.getHeight();
                    }
                    break;
                case 1:
                    this.position[0]+=distance;
                    this.position[1]-=distance;
                    if(this.position[0]>world.getWidth()){
                        this.position[0]=this.position[0]%world.getWidth();
                    }
                    if(this.position[1]<0){
                    this.position[1]=this.position[1]+world.getHeight();
                    }
                    break;
                case 2:
                    this.position[0]-=distance;
                    this.position[1]+=distance;
                    if(this.position[0]<0){
                        this.position[0]=this.position[0]+world.getWidth();
                    }
                    if(this.position[1]>world.getHeight()){
                        this.position[1]=this.position[1]%world.getHeight();
                    }
                    break;
                case 3:
                    this.position[0]-=distance;
                    this.position[1]-=distance;
                    if(this.position[0]<0){
                        this.position[0]=this.position[0]+world.getWidth();
                    }
                    if(this.position[1]<0){
                        this.position[1]=this.position[1]+world.getHeight();
                    }
                    break;
            }
        }
    }


    /**
     *  money system
     */
    public void earnMoney(){
        //only audult MALE can earn money
        if(this.sex.equals(SEX.MALE) && this.age>=18){
            Random moneyRander=new Random();
            int indice=moneyRander.nextInt(2);
            int bonus=moneyRander.nextInt(this.moneyAbility*3);
            //salary=moneyAbility+random bonus
            switch(indice){
                case 0:
                    this.money=this.money+(this.moneyAbility/4)+bonus; break;
                case 1:
                    this.money=Math.max(0,this.money+(this.moneyAbility/4)-bonus); break;
            }
        }
    }




    //print
    public String toString(){
        String outPut="["+this.clanName+"]"+this.sex.name()+": "+this.name;
        if(this.alive==false){
            outPut=outPut+"("+Integer.toString(this.isBornAt())+"-"+Integer.toString(this.isDeadAt())+")"+", dead at ";
        } else {
            outPut=outPut+"("+Integer.toString(this.isBornAt())+"-?)"+", is now ";
        }
        outPut=outPut+Integer.toString(this.age)+" years old."+" [Beauty: "+Integer.toString(this.beauty)+"]"+" [Position: "+Integer.toString(this.position[0])+":"+Integer.toString(this.position[1])+"]"+" [Money: "+Integer.toString(this.money)+"]"+" [Loyalty: "+Integer.toString(this.loyalty)+"]"+" [Flirt: "+Integer.toString(this.flirt)+"]";
        if(this.hasSpouse()){
            outPut=outPut+" (Spouse: "+this.spouse.getName()+" "+this.spouse.clanName+")";
        }
        return outPut;
    }


    /**
     *  simplest private constructor
     *  the other users can not use it.
     */

    private Human(String inputName, String inputClanName, int inputAge, SEX inputSex, int[] inputPosition, int inputBeauty, int inputMoney, boolean inputAlive, int inputAvgLife, int inputBorn, int inputDeath, int inputRange, int inputView, int inputAbility, int inputBabyNum, int inputBabyAbility, String inputChildFather, int inputLoyalty, int inputFlirt){
        this.name=inputName;
        this.clanName=inputClanName;
        this.age=inputAge;
        this.sex=inputSex;
        this.position=inputPosition;
        this.beauty=inputBeauty;
        this.money=inputMoney;
        this.alive=inputAlive;
        this.avgLife=inputAvgLife;
        this.bornAt=inputBorn;
        this.deadAt=inputDeath;
        this.range=inputRange;
        this.view=inputView;
        this.moneyAbility=inputAbility;
        this.babyNum=inputBabyNum;
        this.babyAbility=inputBabyAbility;
        this.childFather=inputChildFather;
        this.loyalty=inputLoyalty;
        this.flirt=inputFlirt;
    }

    private Human(){}


    /**
     *  private fields
     */

    //person arguments
    private String name=null;
    private int age=0;
    private SEX sex=null;
    private Human spouse=null;
    private int[] position={0,0};
    private int beauty=0;
    private boolean alive=true;
    private int bornAt=0;
    private int deadAt=0;
    private int babyNum=0;
    private String childFather=null;
    //clan arguments
    private String clanName=null;
    private int range=0;
    private int view=0; //
    private int avgLife=0;
    private int money=0;
    private int moneyAbility=0;
    private int babyAbility=0;
    private int loyalty=0;
    private int flirt=0;


    /**
     *  MAIN
     *  to test the method
     *  @param args void
     */
    public static void main(String[] args){

    }

}
```

#### 氏族（Clan.java）

```java
/**
 *  Clan.java is a abstract factory of human.
 *  Asign to human some basic capabilities.
 *  7 clans are pre-built
 *      ->1.Rose:       high beauty
 *      ->2.Banker:     rich
 *      ->3.Ranger:     love travel
 *      ->4.Skaven:     higher birth rate
 *      ->5.Playboy:    flirt married women
 *      ->6.Barbarian:  don't care ugly women
 *      ->7.Nobody:     no extra skill
 *  @author wei.shen@iro.umontreal.ca
 *  @version 1.0
 */


package com.ciaoshen.thinkinjava.clanWar;
import java.util.*;

/**
 *  ->All the methods are realized
 *  ->All the clans will inherit the same method to  genetate their members
 *  ->Also package access
 */
abstract class Clan {

    /**
     *  STATIC FIELDS
     */
    public static ArrayList<String> CLAN_NAMES=new ArrayList<String>();

    /**
     *  CONSTRUCTOR: static initialisation block
     */
    static {
        CLAN_NAMES.add("Rose");
        CLAN_NAMES.add("Banker");
        CLAN_NAMES.add("Ranger");
        CLAN_NAMES.add("Skaven");
        CLAN_NAMES.add("Playboy");
        CLAN_NAMES.add("Barbarian");
        CLAN_NAMES.add("Nobody");
    }


    /**
     *  STATIC PROXY OF CONSTRUCTOR
     *  the only public factory method to create clan by their name
     */
    public static Clan createTheClan(String clanName){
        Clan theClan=null;
        switch(clanName){
            case "Rose":
                theClan = new ClanRose(); break;
            case "Banker":
                theClan = new ClanBanker(); break;
            case "Ranger":
                theClan = new ClanRanger(); break;
            case "Skaven":
                theClan = new ClanSkaven(1); break;
            case "Playboy":
                theClan = new ClanPlayboy(); break;
            case "Barbarian":
                theClan = new ClanBarbarian(); break;
            case "Nobody":
                theClan = new ClanNobody(); break;
        }
        return theClan;
    }


    /**
     *  The only public access to Human Facorty to creat humans (different clan members)
     *  World charge clans, and clan create humans
     */

    //use the factory method clanMemberFactory() to decorate humans and insert them to this world
    public void chargeClan(int number, TheWorld world){
        for(int i=0; i<number; i++){
            Human XMan=this.clanMemberFactory(world);
            XMan.bornAt(world.getYear());
            world.getHumanList().add(XMan);
        }
        System.out.println("Clan "+clanName+" are charged!");
    }


    /**
     *  package access tool methods
     */

    //builder method to adjust clan's default parameters before calling human factory method to creat humans
    Clan setAverageBeauty(int averageBeauty){
        this.averageBeauty=averageBeauty;
        return this;
    }
    Clan setBeautyFloat(int beautyFloat){
        this.beautyFloat=beautyFloat;
        return this;
    }
    Clan setMoney(int money){
        this.money=money;
        return this;
    }
    Clan setMoneyAbility(int moneyAbility){
        this.moneyAbility=moneyAbility;
        return this;
    }

    Clan setClanAvgLife(int clanAvgLife){
        this.clanAvgLife=clanAvgLife;
        return this;
    }

    Clan setInitialRange(int initialRange){
        this.initialRange=initialRange;
        return this;
    }

    Clan setInitialView(int initialView){
        this.initialView=initialView;
        return this;
    }

    Clan setBabyAbility(int babyAbility){
        this.babyAbility=babyAbility;
        return this;
    }

    Clan setInitialLoyalty(int initialLoyalty){
        this.initialLoyalty=initialLoyalty;
        return this;
    }

    Clan setLoyaltyFloat(int loyaltyFloat){
        this.loyaltyFloat=loyaltyFloat;
        return this;
    }

    Clan setInitialFlirt(int initialFlirt){
        this.initialFlirt=initialFlirt;
        return this;
    }

    Clan setFlirtFloat(int flirtFloat){
        this.flirtFloat=flirtFloat;
        return this;
    }

    Clan setClanName(String clanName){
        this.clanName=clanName;
        return this;
    }

    //The factory method of human with different characteristics
    Human clanMemberFactory(TheWorld world){
        return Human.createNewMan(Name.randomName(), this.clanName, 0, SEX.randomSex(), randomPosition(world.getWidth(),world.getHeight()), randomBeauty(this.beautyFloat), this.money, true, this.clanAvgLife, world.getYear(), 0, this.initialRange, this.initialView, this.moneyAbility, 0, this.babyAbility, null, randomLoyalty(this.loyaltyFloat), this.randomFlirt(this.flirtFloat));
    }


    //get a random beauty acording to the average beauty
    //we can set the floating range
    int randomBeauty(int floatingBeauty){
        int beauty=0;
        //more or less beauty?
        Random myRander = new Random();
        int luck=myRander.nextInt(2);
        int offset=myRander.nextInt(floatingBeauty+1);
        switch (luck){
            case 0: beauty=Math.max(0,this.averageBeauty-offset); break;  //ugly
            case 1: beauty=Math.min(100,this.averageBeauty+offset); break;  //beautiful
        }
        return beauty;
    }

    //give each person a random position
    int[] randomPosition(int height, int width){
        Random myRander = new Random();
        int x=myRander.nextInt(width+1);
        int y=myRander.nextInt(height+1);
        int[] position = {x,y};
        return position;
    }

    //give each person a random flirt value
    int randomFlirt(int flirtFloat){
        Random flirtRander=new Random();
        int floatFlirt=flirtRander.nextInt(flirtFloat+1);
        return Math.min(100,this.initialFlirt+floatFlirt);
    }

    //give each person a random flirt value
    int randomLoyalty(int loyaltyFloat){
        Random loyaltyRander=new Random();
        int floatLoyalty=loyaltyRander.nextInt(loyaltyFloat+1);
        return Math.min(100,this.initialLoyalty+floatLoyalty);
    }

    /**
     *  Package access constructor
     *  Need to be inherited in the package
     */
    Clan(){}
    /**
     *  Package access fields
     *  Need to be inherited in the package
     */
    int averageBeauty=50;
    int beautyFloat=50;
    int money=0;
    int moneyAbility=50;
    int clanAvgLife=60;
    int initialRange=20;
    int initialView=50;
    int babyAbility=2;
    int initialLoyalty=40;
    int loyaltyFloat=20;
    int initialFlirt=0;
    int flirtFloat=40;
    String clanName="Nobody";
}

```

#### 七大种族
ClanRose.java, ClanBanker.java, ClanRanger.java, ClanSkaven.java, ClanPlayboy.java, ClanBarbarian.java, ClanNobody.java

这里只以Rose族为例：
```java
/**
 *  Clan Beautiful is the most beautiful clan in the world.
 *  Each member has 20 beauty points as bonus: Average beauty = 70
 *  @author wei.shen@iro.umontreal.ca
 *  @version 1.0
 */

package com.ciaoshen.thinkinjava.clanWar;
import java.util.*;


/**
 *  Only package access
 */
class ClanRose extends Clan{

    /**
     *  public constructor
     */
    //default sets
    public ClanRose(){
        this.clanName="Rose";
        this.averageBeauty=80;
    }

    //special sets
    public ClanRose(int beauty){
        this.clanName="Rose";
        this.averageBeauty=beauty;
    }

    /**
     *  MAIN
     */
    public static void main(String[] args){
    }

}
```

#### 大陆（TheWorld.java）

```java
/**
 *  The clan war take place in this world.
 *  @author wei.shen@iro.umontreal.ca
 *  @version 1.0
 */

package com.ciaoshen.thinkinjava.clanWar;
import java.util.*;

//used to control the time pass
class TheWorld {
    /**
     *  public interface of constructor
     */
    public static TheWorld createNewWorld(){
        return new TheWorld();
    }
    public static TheWorld createNewWorld(int width, int height){
        return new TheWorld(0, width, height);
    }


    /**
     *  Calendar system
     */
    public void passOneYear(){
        //begin to flirt
        this.spring();
        //then pass the rest of the year
        this.year+=1;
        System.out.print(this.year+"    ");
        ArrayList<Human> newBorn=new ArrayList<Human>();
        for(Iterator<Human> it = this.humanList.iterator();it.hasNext();){
            Human h=it.next();
            //beauty system
            if(h.getAge()>15 && h.getAge()<40){
                h.growth();
            }
            if(h.getAge()>=40){
                h.fading();
            }
            //money system
            h.earnMoney();
            //mother give birth
            if(h.getSex().equals(SEX.FEMALE) && h.getBabyNum()>0){
                Human newBaby=h.babyBirth(this);
                newBorn.add(newBaby); //world is blocked during the iteration
            }
            //age and death system
            h.addAge(1);
            if(h.randomDeath()){
                h.toDeath();
                h.deadAt(this.year);
                this.heaven.add(h);
                it.remove();
            }
        }
        for(Human nh : newBorn){
            this.humanList.add(nh);
            //System.out.println(h.toString());
        }
    }


    /**
     *  flirt system
     */
    public void spring(){
        for (Human h : this.humanList){
            //male move
            h.move(this);
            //female flirt
            if(h.getSex().equals(SEX.FEMALE)){
                if(!h.hasSpouse()){
                    h.girlFlirt(this);
                }else{
                    h.havingAffair(this);
                }
            }
        }
    }

    //allow us to add human to the world
    public void addBaby(Human baby){
        this.humanList.add(baby);
    }


    /**
     *  Print the world
     */

    public void printSurvivors(){
        for (Human h : this.humanList){
            System.out.println(h.toString());
        }
    }
    public void printGhosts(){
        for (Human h : this.heaven){
            System.out.println(h.toString());
        }
    }
    public void printWorld(){
        for (Human h : this.humanList){
            System.out.println(h.toString());
        }
        for (Human h : this.heaven){
            System.out.println(h.toString());
        }
    }


    /**
     *  show arguments
     */

    public int getYear(){return this.year;}
    public int getWidth(){return this.width;}
    public int getHeight(){return this.height;}
    public ArrayList<Human> getHumanList(){return this.humanList;}
    public ArrayList<Human> getHeaven(){return this.heaven;}


    /**
     *  private constructor
     */
    private TheWorld(){}
    private TheWorld(int year, int width, int height){
        this.year=year;
        this.width=width;
        this.height=height;
    }

    /**
     *  private fields
     */
    private int year=0;
    private int width=1000;
    private int height=1000;
    //the world is a container of human
    private ArrayList<Human> humanList=new ArrayList<Human>();
    private ArrayList<Human> heaven=new ArrayList<Human>();


    /**
     *  MAIN
     *  @param args void
     */
    public static void main(String[] args){

    }
}

```

#### 氏族战争（WarOfClan.java）

```java
/**
 *  A cruel natural selection game
 *  7 clans are pre-built
 *      ->1.Rose:       high beauty
 *      ->2.Banker:     rich
 *      ->3.Ranger:     love travel
 *      ->4.Skaven:     higher birth rate
 *      ->5.Playboy:    flirt married women
 *      ->6.Barbarian:  don't care ugly women
 *      ->7.Nobody:     no extra skill
 *  @author wei.shen@iro.umontreal.ca
 *  @version 1.0
 */

package com.ciaoshen.thinkinjava.clanWar;
import java.util.*;

/**
 *  This is a factory class to create the hole world and human, and then pass several years.
 *  It use the factory class "Clan" to generate humans.
 */
public class WarOfClan {
    /**
     *  Main fonction
     */
    //automaticly run the game and return the statistic result
    public static ArrayList<ArrayList<Double>> runTheGame(int years, ArrayList<Integer> iniPopulations){
        //initialize the container of the result
        ArrayList<ArrayList<Double>> result=new ArrayList<ArrayList<Double>>();
        //create world
        TheWorld newWorld = TheWorld.createNewWorld();

        //create the factories of humans: Clans
        int clanNumber=0;   //total number of clans
        for(String clanName : Clan.CLAN_NAMES){
            Clan theClan=Clan.createTheClan(clanName);
            //each clan create their members(human)
            if(iniPopulations.size()>clanNumber){
                theClan.chargeClan(iniPopulations.get(clanNumber),newWorld);
                //create the result container
                result.add(new ArrayList<Double>());
                clanNumber++;
            }
        }

        //XXX years later
        for(int i=0;i<years;i++){
            newWorld.passOneYear();
            //statistic results: population of each clan at the end of each year
            ArrayList<Double> oneYearResult=collectResult(newWorld,clanNumber);
            for(int j=0;j<clanNumber;j++){
                result.get(j).add(oneYearResult.get(j));
            }
        }
        return result;
    }

    /**
     *  Statistic
     */
    //return the statistic results: population of each clan at the end of each year
    public static ArrayList<Double> collectResult(TheWorld world, int clanNumber){
        //collect the result
        ArrayList<Double> result=new ArrayList<Double>();
        for(int i=0;i<clanNumber;i++){
            result.add(0.0);
        }
        //statistic
        for (Human h : world.getHumanList()){
            //clan population plus 1
            if(Clan.CLAN_NAMES.indexOf(h.getClanName())!=-1){
                result.set(Clan.CLAN_NAMES.indexOf(h.getClanName()),result.get(Clan.CLAN_NAMES.indexOf(h.getClanName()))+1);
            }
        }
        return result;
    }


    /**
     *  MAIN
     *  to test the method
     *  @param args void
     */
    public static void main(String[] args){

    }
}
```

#### 图形板（GraphPanel.java）

```java
/**
 *  Help me to draw a graph to show the statistic result
 *  GraphPanel is a data package
 *  paintComponent() methods records all the rules to draw the graph
 *  createAndShowGui() use JFrame to call paintComponent() to draw the graph
 *  @author Rodrigo Castro
 *  @version 1.0
 */

package com.ciaoshen.thinkinjava.clanWar;

import java.lang.Math;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;



public class GraphPanel extends JPanel {

    private int width = 800;
    private int heigth = 400;
    private int padding = 35;
    private int labelPadding = 25;
    private int referenceZone = 100;    //show the title of each line
    private int referencePadding = 50;
    /*we random the line and point color for each line*/
    //private Color lineColor = new Color(44, 102, 230, 180);
    //private Color pointColor = new Color(100, 100, 100, 180);
    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private int pointWidth = 4;
    private int numberYDivisions = 10;
    private ArrayList<ArrayList<Double>> result;
    private ArrayList<String> ticket;
    private static final long serialVersionUID = 0L; //useless, just because JPanel implements the serializable interface

    public GraphPanel(ArrayList<ArrayList<Double>> result, ArrayList<String> ticket) {
        this.result = result;
        this.ticket = ticket;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        //get the length of the tickets
        int colonWidth = getMaxTicket(g2, ticket);
        //calculate the number of colone needed
        int colonNum=1; //more than one colon with too much lines references
        int maxRefEachColon = (int)Math.floor((double)(getHeight() - 2 * padding - labelPadding) / referencePadding);
        colonNum = (int)Math.ceil(result.size()/(double)maxRefEachColon);
        //get the height of the reference zone
        int colonHeight=0;
        if(colonNum==1){
            colonHeight=padding + referencePadding + result.size() * referencePadding;
        } else if(colonNum>1){
            colonHeight=getHeight()-padding*2-labelPadding;
        }

        //calculate the width of reference zone
        this.referenceZone=(colonNum*colonWidth+(colonNum-1)*padding/2);

        //calculate the size of each small cell
        double xScale = ((double) getWidth() - padding - labelPadding - padding - referenceZone - padding) / (result.get(0).size() - 1);
        double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (getMaxScore() - getMinScore());


        // draw the main white background
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding - referenceZone - padding, getHeight() - 2 * padding - labelPadding);
        // draw the small white background for the reference zone
        g2.fillRect(getWidth() - padding - referenceZone - padding/2, padding, referenceZone + padding, colonHeight);
        g2.setColor(Color.BLACK);


        // create hatch marks and grid lines for y axis.
        for (int i = 0; i < numberYDivisions + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
            int y1 = y0;
            if (result.get(0).size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding -referenceZone - padding, y1);
                g2.setColor(Color.BLACK);
                String yLabel = ((int) ((getMinScore() + (getMaxScore() - getMinScore()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }


        // and for x axis
        for (int i = 0; i < result.get(0).size(); i++) {
            if (result.get(0).size() > 1) {
                int x0 = i * (getWidth() - padding * 2 - labelPadding - referenceZone -padding) / (result.get(0).size() - 1) + padding + labelPadding;
                int x1 = x0;
                int y0 = getHeight() - padding - labelPadding;
                int y1 = y0 - pointWidth;
                if ((i % ((int) (result.get(0).size() / 20.0) + 1)) == 0) {
                    g2.setColor(gridColor);
                    g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
                    g2.setColor(Color.BLACK);
                    String xLabel = i + "";
                    FontMetrics metrics = g2.getFontMetrics();
                    int labelWidth = metrics.stringWidth(xLabel);
                    g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                }
                g2.drawLine(x0, y0, x1, y1);
            }
        }

        // create x and y axes
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding -referenceZone - padding, getHeight() - padding - labelPadding);


        int index=0; //my own List index
        for(ArrayList<Double> scores : result){
            //collect the points of one line
            List<Point> graphPoints = new ArrayList<>();
            for (int i = 0; i < scores.size(); i++) {
                int x1 = (int) (i * xScale + padding + labelPadding);
                int y1 = (int) ((getMaxScore() - scores.get(i)) * yScale + padding);
                graphPoints.add(new Point(x1, y1));
            }

            //draw each line
            Stroke oldStroke = g2.getStroke();
            Random colorRander=new Random();
            int color1=colorRander.nextInt(256);
            int color2=colorRander.nextInt(256);
            int color3=colorRander.nextInt(256);
            //alpha value = 255 (no transparent)
            Color lineColor = new Color(color1,color2,color3,255);
            g2.setColor(lineColor);
            g2.setStroke(GRAPH_STROKE);
            for (int i = 0; i < graphPoints.size() - 1; i++) {
                int x1 = graphPoints.get(i).x;
                int y1 = graphPoints.get(i).y;
                int x2 = graphPoints.get(i + 1).x;
                int y2 = graphPoints.get(i + 1).y;
                g2.drawLine(x1, y1, x2, y2);
            }
            //draw the reference of the line to the panel
            int colonId=(int)Math.ceil((double)(index+1)/maxRefEachColon);
            int x0 = getWidth() - padding/2 - (colonNum-colonId+1)*(colonWidth+padding/2);
            int x1 = x0+colonWidth;
            int y0 = padding + referencePadding * (index%maxRefEachColon) + referencePadding;
            int y1 = y0;
            g2.drawLine(x0, y0 - pointWidth, x0, y0);
            g2.drawLine(x0, y0, x1, y1);
            g2.drawLine(x1, y0 - pointWidth, x1, y0);
            g2.drawString(ticket.get(index),x0,y0 +referencePadding/3);


            //draw the point?? smoothing?? color??
            g2.setStroke(oldStroke);
            g2.setColor(lineColor); //we use lineColor to set pointColor
            for (int i = 0; i < graphPoints.size(); i++) {
                int x = graphPoints.get(i).x - pointWidth / 2;
                int y = graphPoints.get(i).y - pointWidth / 2;
                int ovalW = pointWidth;
                int ovalH = pointWidth;
                g2.fillOval(x, y, ovalW, ovalH);
            }
            index++;
        }
    }

    //    @Override
    //    public Dimension getPreferredSize() {
    //        return new Dimension(width, heigth);
    //    }

    //get the longest ticket to set the width of reference zone
    private static int getMaxTicket(Graphics2D g2, ArrayList<String> ticket){
        FontMetrics metrics = g2.getFontMetrics();
        int maxTicket=0;
        for(String t : ticket){
            int ticketWidth=metrics.stringWidth(t);
            maxTicket=Math.max(maxTicket,ticketWidth);
        }
        return maxTicket;
    }

    private double getMinScore() {
        double minScore = Double.MAX_VALUE;
        for(ArrayList<Double> oneSerie : result){
            for (Double score : oneSerie) {
                minScore = Math.min(minScore, score);
            }
        }
        return minScore;
    }

    private double getMaxScore() {
        double maxScore = Double.MIN_VALUE;
        for(ArrayList<Double> oneSerie : result){
            for (Double score : oneSerie) {
                maxScore = Math.max(maxScore, score);
            }
        }
        return maxScore;
    }

    public void setScores(ArrayList<ArrayList<Double>> result) {
        this.result = result;
        invalidate();
        this.repaint();
    }

    public ArrayList<ArrayList<Double>> getScores() {
        return result;
    }

    public static void createAndShowGui(ArrayList<ArrayList<Double>> result, ArrayList<String> ticket) {
        GraphPanel mainPanel = new GraphPanel(result,ticket);
        mainPanel.setPreferredSize(new Dimension(1400, 800));
        JFrame frame = new JFrame("DrawGraph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //configurate the game
        int years=500;
        ArrayList<Integer> iniPopulations=new ArrayList<Integer>();
        iniPopulations.add(new Integer(2000));  //rose
        iniPopulations.add(new Integer(2000));  //banker
        iniPopulations.add(new Integer(2000));  //ranger
        iniPopulations.add(new Integer(2000));  //skaven
        iniPopulations.add(new Integer(2000));  //playboy
        iniPopulations.add(new Integer(2000));  //barbarian
        iniPopulations.add(new Integer(2000));  //nobody

        //run the WarOfClan game
        ArrayList<ArrayList<Double>> result=WarOfClan.runTheGame(years,iniPopulations);

        //draw the statistic graph of the game
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGui(result,Clan.CLAN_NAMES);
            }
        });
    }
}
```
