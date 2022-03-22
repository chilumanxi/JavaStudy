# OOP #

## 概念 ##

面向对象编程的英文缩写是OOP，全称是Object Oriented Programming。

两个非常重要的概念：

1. 面向对象编程
2. 面向对象编程语言

两者没有确切定义，可以概括如下：

-  面向对象编程是一种编程范式或编程风格。它以类或对象作为组织代码的基本单元，并将**封装、抽象、继承、多态**四个特性，作为代码设计和实现的基石 。
-  面向对象编程语言是支持类或对象的语法机制，并有现成的语法机制，能方便地实现面向对象编程四大特性（封装、抽象、继承、多态）的编程语言。

面向对象编程从字面上，按照最简单、最原始的方式来理解，就是将对象或类作为代码组织的基本单元，来进行编程的一种编程范式或者编程风格，并不一定需要封装、抽象、继承、多态这四大特性的支持

还有两个概念：

1. 面向对象分析 Object Oriented Analysis 简称为OOA
2. 面向对象设计 Object Oriented Design 简称为OOD

在前面加上“面向对象”几个字，是因为这几个过程，都是围绕对象或者类来做的需求分析和设计。分析和设计最终产出的是类的设计，包括程序被拆解为哪些类、每个类有哪些方法，类与类之间该如何交互。


## 面向对象编程的四个特性 ##

### 封装 ###

封装也叫做信息隐藏或者数据访问保护，类通过暴露有限的访问接口，授权外部仅能通过类提供的方式来访问内部信息或者数据。

参考代码如下：

    
	public class Wallet {
  		private String id;						//钱包唯一编号
  		private long createTime;					//钱包创建时间
  		private BigDecimal balance;				//钱包余额
  		private long balanceLastModifiedTime;	//上次钱包余额变更时间
  		// ...省略其他属性...

  		public Wallet() {
   	    this.id = IdGenerator.getInstance().generate();
     	this.createTime = System.currentTimeMillis();
     	this.balance = BigDecimal.ZERO;
     	this.balanceLastModifiedTime = System.currentTimeMillis();
		}

  		public String getId() { return this.id; }
  		public long getCreateTime() { return this.createTime; }
  		public BigDecimal getBalance() { return this.balance; }
  		public long getBalanceLastModifiedTime() { return this.balanceLastModifiedTime;  }

  		public void increaseBalance(BigDecimal increasedAmount) {
  			if (increasedAmount.compareTo(BigDecimal.ZERO) < 0) {
      			throw new InvalidAmountException("...");
    	  	}
    			this.balance.add(increasedAmount);
	    		this.balanceLastModifiedTime = System.currentTimeMillis();
  		}
	
  		public void decreaseBalance(BigDecimal decreasedAmount) {
			if (decreasedAmount.compareTo(BigDecimal.ZERO) < 0) {
    	  			throw new InvalidAmountException("...");
    			}
			if (decreasedAmount.compareTo(this.balance) > 0) {
				throw new InsufficientAmountException("...");
   		 	}
			this.balance.subtract(decreasedAmount);
	 		this.balanceLastModifiedTime = System.currentTimeMillis();
  		}
	}

参照封装特性，钱包四个属性仅提供了六个函数来访问或者修改钱包的余额。这样设计的原因是，id和createTime在类创建伊始就确定了也不会再进行改变了，故不涉及set方法，而balanceLastModifiedTime随balance的改变一起改变，所以将其的修改操作封装在对钱包余额进行修改的函数里，这样也可以保证这两者的一致性。

Java提供了访问权限控制的语法机制，如private，来支持封装特性。通过封装这一特性，有效的提高了代码的可读性和可维护性，同事也能提高类的易用性（毕竟较少的接口了解起来比较多的接口要更简单）

### 抽象 ###

抽象旨在隐藏方法的具体实现，让调用者只需要关心方法提供了哪些功能，并不需要知道这些功能怎么实现的。

如Java中的interface接口类和abstract抽象类，均可体现这一特性。如下面这个接口类的例子：

    
    public interface IPictureStorage {
      void savePicture(Picture picture);
      Image getPicture(String pictureId);
      void deletePicture(String pictureId);
      void modifyMetaInfo(String pictureId, PictureMetaInfo metaInfo);
    }
    
    public class PictureStorage implements IPictureStorage {
      // ...省略其他属性...
      @Override
      public void savePicture(Picture picture) { ... }
      @Override
      public Image getPicture(String pictureId) { ... }
      @Override
      public void deletePicture(String pictureId) { ... }
      @Override
      public void modifyMetaInfo(String pictureId, PictureMetaInfo metaInfo) { ... }
    }

使用者只需要关心IPictureStorage中方法是做什么的就可以，不需要看具体实现。其实函数也是一种抽象概念的体现，因为不是代码中直接实现，而是抽象为一个函数，抽象是一个比较宏观的概念。

换一个角度考虑，我们在定义类的方法时，要尽量避免暴露太多细节，不要太具体。


### 继承 ###

分为单继承（只继承于一个父类）和多继承（继承于多个父类）。多继承一般不被新语言锁支持，原因在于容易造成代码逻辑甚至业务逻辑的混乱，而多重继承能带来的好处，可以通过扩展多个接口抵消掉大部分，用好多重继承，对编码能力提出了更高对要求，基于此，编程语言出于易学易用的目的，就足以放弃对多重继承的支持。

继承最大的好处在于代码复用，但是这一点也不是继承所独有的，组合也可以实现。过度使用继承会使继承层次过深过复杂，同样如果子类和父类高度耦合，父类修改会影响到子类。


### 多态 ###

多态是指，子类可以替换父类，在实际的代码运行过程中，调用子类的方法实现，如父类中有个add函数，为添加数到数组中，子类可以继承父类并重写add函数，将其改变为有序添加的函数。使用创建父类对象引用子类对象，使用子类对象中重写的add函数，实现有序添加，具体代码详见Polymorphism

多态还可以利用接口类语法体现特性，代码如下：

    public interface Iterator {
        boolean hasNext();
        String next();
        String remove();
    }
    
    public class Array implements Iterator {
        private String[] data;
    
        public boolean hasNext() { ... }
        public String next() { ... }
        public String remove() { ... }
        //...省略其他方法...
    }
    
    public class LinkedList implements Iterator {
        private LinkedListNode head;
    
        public boolean hasNext() { ... }
        public String next() { ... }
        public String remove() { ... }
        //...省略其他方法... 
    }
    
    public class Demo {
        private static void print(Iterator iterator) {
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        }
    
        public static void main(String[] args) {
            Iterator arrayIterator = new Array();
            print(arrayIterator);
    
            Iterator linkedListIterator = new LinkedList();
            print(linkedListIterator);
        }
    }

这段代码中，Array和LinkedList类都实现了接口类Iterator。通过传递不同类型的实现类（Array、LinkedList）到 print(Iterator iterator) 函数中，支持动态的调用不同的 next()、hasNext() 实现。这样当我们需要新加一种数据结构类型时，只要再新写一种实现Iterator的类型就可以，不需要改变print的内容。多态性提高了代码的复用性。

## OO和PO比较 ##

面向过程编程 Procedure Oriented Programming 简称POP

给出大概的定义：

- 面向过程编程也是一种编程范式或编程风格。它以过程（可以理解为方法、函数、操作）作为组织代码的基本单元，以数据（可以理解为成员变量、属性）与方法相分离为最主要的特点。面向过程风格是一种流程化的编程风格，通过拼接一组顺序执行的方法来操作数据完成一项功能。
- 面向过程编程语言首先是一种编程语言。它最大的特点是不支持类和对象两个语法概念，不支持丰富的面向对象编程特性（比如继承、多态、封装），仅支持面向过程编程。

面向过程和面向对象最基本的区别就是，代码的组织方式不同。面向过程风格的代码被组织成了一组方法集合及其数据结构，方法和数据结构的定义是分开的。面向对象风格的代码被组织成一组类，方法和数据结构被绑定一起，定义在类中。

面向过程会把需求划分为若干个步骤过程，逐句翻译成代码，对于大规模复杂的软件开发来说，整个程序处理流程错综复杂，并非只有一条主线，如果把整个流程画出来可能是一张网状结构，此时再拆分为过程会十分困难。而面向对象编程可以先思考如何给业务建模，如何将需求翻译成类，如何给类之间确立关系。这些思考设计好后，可以像搭积木一样实现。

相比于面向对象编程的四大特性，面向过程只具备抽象，代码扩展性，可读性，易维护性差。从人的角度出发，面向对象编程是一个更符合人思考流程的方式，更加人性化，更加智能。


### 常见的OOP和POP的使用混淆 ###

1. getter和setter的滥用。 要注意两者使用时的封装性特点，参考WrongUseOfOOP的EncapsulationError.java
2. 全局变量和全局方法的滥用。这之中Constants 类和 Utils类最常用到。对于Constants类，一是要注意细分，将不同类的常量放到不同的Constants中，如果所有的常量都放到一个类中，会导致代码的复用性降低，编译时间增加。二是可以将其定义在需要使用的类中。对于Utils类，是一种面向过程的思考方式，要注意思考是否需要一个Utils类来实现某个通用功能。
3. 定义数据和方法分离的类。传统的MVC结构会出现这样的编程风格。

### POP仍然可以使用 ###

如果我们开发的是微小程序，或者是一个数据处理相关的代码，以算法为主，数据为辅，那脚本式的面向过程的编程风格就更适合一些。当然，面向过程编程的用武之地还不止这些。实际上，面向过程编程是面向对象编程的基础，面向对象编程离不开基础的面向过程编程，面向对象中每个方法的实现也是面向过程的。

最终的目的还是开发出易维护、易读、易复用、易扩展的高质量代码


### 抽象类和接口类 ###

抽象类不允许被实例化，只能被继承。它可以包含属性和方法。方法既可以包含代码实现，也可以不包含代码实现。不包含代码实现的方法叫作抽象方法。子类继承抽象类，必须实现抽象类中的所有抽象方法。

接口类不能包含属性，只能声明方法，方法不能包含代码实现。类实现接口的时候，必须实现接口中声明的所有方法。

如果我们要表示一种 is-a 的关系，并且是为了解决代码复用的问题，我们就用抽象类；如果我们要表示一种 has-a 关系，并且是为了解决抽象而非代码复用的问题，那我们就可以使用接口。

从类的继承层次上来看，抽象类是一种自下而上的设计思路，先有子类的代码重复，然后再抽象成上层的父类（也就是抽象类）。而接口正好相反，它是一种自上而下的设计思路。我们在编程的时候，一般都是先设计接口，再去考虑具体的实现。

我们在做软件开发的时候，一定要有抽象意识、封装意识、接口意识。在定义接口的时候，不要暴露任何实现细节。接口的定义只表明做什么，而不是怎么做。而且，在设计接口的时候，我们要多思考一下，这样的接口设计是否足够通用，是否能够做到在替换具体的接口实现的时候，不需要任何接口定义的改动。


### 继承和组合 ###

继承会出现问题的情况：

1. 当继承关系过于复杂时，类的继承层次会越来越深、继承关系会越来越复杂。而这种层次很深、很复杂的继承关系，一方面，会导致代码的可读性变差。因为我们要搞清楚某个类具有哪些方法、属性，必须阅读父类的代码、父类的父类的代码……一直追溯到最顶层父类的代码。另一方面，这也破坏了类的封装特性，将父类的实现细节暴露给了子类。子类的实现依赖父类的实现，两者高度耦合，一旦父类代码修改，就会影响所有子类的逻辑。
2. 当两个类都用到了某个功能，如果将两个不相关的类僵硬的抽象出来一个父类，并写一个莫名其妙的实现方法，会显得很莫名奇妙

针对以上的情况，使用组合就非常的合适。但是我们还是要根据具体的情况，来选择该用继承还是组合。如果类之间的继承结构稳定，层次比较浅，关系不复杂，我们就可以大胆地使用继承。反之，我们就尽量使用组合来替代继承。除此之外，还有一些设计模式、特殊的应用场景，会固定使用继承或者组合。

### 基于贫血模型的MVC架构 ###

MVC 三层架构中的 M 表示 Model，V 表示 View，C 表示 Controller。它将整个项目分为三层：展示层、逻辑层、数据层。这是一个比较笼统的分层方式，具体开发层面上，会根据项目的需求做适当的调整。

比如很多Web或者App项目都是前后端分离的，后端负责暴露接口给前端调用，这种情况下，我们一般将后端项目分为Repository（数据访问层）、Service（业务逻辑层）和Controller（负责暴露接口）三层。

贫血模型是指只包含数据，不包含业务逻辑的类破坏了面向对象封装的特性。

充血模型（Rich Domain Model）是指数据和对应的业务逻辑被封装到同一个类中，满足OOP封装的特性。它跟基于贫血模型的传统开发模式的区别主要在 Service 层。

领域驱动设计（Domain Driven Design，简称 DDD），主要是用来指导如何解耦业务系统，划分业务模块，定义业务领域模型及其交互。因为微服务而被大众熟知。Service 层包含 Service 类和 Domain 类两部分，Domain 就相当于贫血模型中的 BO。不过，Domain 与 BO 的区别在于它是基于充血模型开发的，既包含数据，也包含业务逻辑。而 Service 类变得非常单薄。

贫血模型的缺陷：平时大部分的开发，都基于SQL驱动的开发模式，接到需求，先思考SQL语句，然后定义Entity、BO、VO，然后往Repository、Service和Controller类中添加代码。当我们开发一个新功能时，只能重写一个满足需求的SQL语句，导致会出现多个长得差不多但是区别很小的SQL。这样会导致复杂系统中代码维护性降低。

充血模型DDD的开发模式：对需求，需要先理清楚所有业务，定义领域模型所包含的属性和方法。领域模型相当于可复用的业务中间层。

