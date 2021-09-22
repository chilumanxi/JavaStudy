# Unit Testing 单元测试

## 背景

​	在过去二十年，单元测试在不断被推动和接受。现如今，单元测试已被大部分公司重视并作为强制性要求。当涉及到企业级应用开发时，几乎每个项目都至少包括若干单元测试，并达成了良好的代码覆盖率。产品代码和测试代码量的比例在1:1到1:3之间，甚至有的时候会到巨大的1:10的比例。随着技术的发展，对于单元测试的讨论从“我们是否需要写单元测试”变成了“如何写好单元测试”，这也是现在主要的困惑所在。

## 单元测试的目标

​	我们常说，单元测试会有助于程序更好的设计，但这只是单元测试的一个副产物，并不是主要的目标。

​	进行单元测试是检验一段代码好坏的试金石。如果你发现你的代码很难进行单元测试，那么就表明你的代码需要改进，常见问题如耦合过紧，因为无法解耦导致无法单独进行单元测试。但是单元测试是一个负面指标，如果你可以轻易地对代码进行单元测试也并不能证明你的代码质量没有问题。

​	进行单元测试的目标是使得项目可持续发展。下图是有无单元测试的项目开发时间区别。

​	![image-20210722123857747](C:\Users\zhanghaoran25\AppData\Roaming\Typora\typora-user-images\image-20210722123857747.png)

​	单元测试可以破坏这种开发时间的上升趋势，即使你为了适应新需求加了新的功能或者进行了代码重构。没有单元测试的帮助，代码开发很难做到拓展。而可持续发展和可拓展性是关键，这两者可以保证开发速度。

​	不要为了写单元测试而写单元测试，要清楚的了解其对项目是否有帮助。不好的单元测试将不能捕捉抛出的错误、或者引发误报、亦或是速度慢难以维护。测试并不是越多越好，代码量是一种负担而不是资产，代码量越多，潜在的错误越多，项目的维护成本越高。单元测试也是代码，它的作用是确保程序的正确性，但是它也需要维护，也会有BUG。

## 覆盖率统计

### 代码覆盖率(code coverage)

$$
代码（单元测试）覆盖率=\frac{执行的代码行数}{代码总行数}
$$

```
public static bool IsStringLong(string input)
{
	if (input.Length > 5)	//Covered by the test
		return true;		//Not covered by the test
	return false;			//Covered by the test
}

public static bool IsStringLong(string input)
{
	return input.Length > 5;//Covered by the test
}

public void Test()
{
	bool result = IsStringLong("abc");
	Assert.Equal(false, result);
}
```

您的代码越紧凑，测试覆盖率指标就越好，因为它只考虑行数量。

### 分支覆盖率(branch coverage)

区别于代码覆盖量按照代码行数做比例，分支覆盖注重用控制结构做比例划分，如if、switch，它显示了整个测试套件中中至少一个测试遍历了多少这样的控制结构。
$$
分支覆盖率=\frac{执行的分支}{总分支}
$$
要计算分支覆盖率，就要计算总共的分支数量，并且看有多少是测试遍历过的。如上文的例子中，IsStringLong方法的覆盖率就是百分之50%。分支覆盖率指标只考虑分支数量； 它没有考虑实现这些分支需要多少行代码。

### 覆盖率的问题

#### 问题一：没法保证所有的输出都被覆盖

```
public static bool WasLastStringLong { get; private set; }
public static bool IsStringLong(string input)
{
	bool result = input.Length > 5;
	WasLastStringLong = result;		//第一个输出
	return result;					//第二个输出
}

public void Test()
{
	bool result = IsStringLong("abc");
	Assert.Equal(false, result);	//单元测试只验证了第二个输出
}
```

​	以下的一段代码的代码覆盖率和分支覆盖率都是100%，但是因为没有证明任何结论，所以这是没有意义的。

```
public void Test()
{
	bool result1 = IsStringLong("abc");
	bool result2 = IsStringLong("abcdef");
}
```

#### 问题二：没有覆盖指标可以涵盖引用的外部库的代码

```
public static int Parse(string input)
{
	return int.Parse(input);
}
public void Test()
{
	int result = Parse("5");
	Assert.Equal(5, result);
}
```

可能有以下几种情况，但是很难知晓，很多边缘情况在测试时无法考虑周全。

![image-20210722181644918](C:\Users\zhanghaoran25\AppData\Roaming\Typora\typora-user-images\image-20210722181644918.png)

并不是说覆盖率指标应该考虑到外部库代码（实际上也不应该考虑），而是表明不能依赖代码覆盖率指标来确定你的测试是否详细和足够完整。
综上所述，单元测试的覆盖率更应该作为一个指标，而不是一个目标。就好比医院会根据病人体温过高这一指标去认定他是否发烧，但是不会说发现体温过高就以降低体温为目标去治疗（总不能低温强行让人降温来治疗发烧吧）。所以低覆盖率一定是有问题的，说明有很多代码没有被测试到，但是高代码覆盖率并不能证明任何事情，提高代码覆盖率应该只是做好单元测试的第一步。

## 优秀单元测试的特性

- 集成到开发周期中。
  理想化的单元测试是代码每经过一次修改，即使是很小的修正，也要进行一次单元测试。自动化测试的唯一要点就可以经常的进行单元测试。
- 仅针对代码库中最重要的部分
  正如不是每个单元测试都相同重要一样，你的代码也不是每一部分都值得去获得同样的单元测试的关注度。对于大部分的项目来说，最重要的部分就是包含了业务逻辑的部分，这一部分值得花时间认真进行单元测试的设计。
  对于其他部分代码，一般可以分为三类：①基础设施代码，部分基础设施代码需要进行测试，比如某些包含复杂、重要算法的代码，那就有必要进行大量单元测试的验证。但是通常情况下，还是应该把重心放在业务逻辑代码的测试中去②外部服务和依赖，诸如数据库和外部系统③用于粘合各部分的代码。
  因此，需要将代码的领域模型部分尽量与其他非必要部分进行代码分离，方便进行单元测试。
- 以最低的维护成本提供最大的价值

## 单元测试的定义

单元测试的定义有很多，所有定义中都包含三个重要的属性：

一个单元测试是一个①验证一部分代码（也叫作单元）②快速运行③以隔离方式运行的自动化测试。

关于单元测试不同定义衍生出两种不同的单元测试方法：

### London Schools

​	在对被测系统（system under test，简称SUT）进行单元测试（Unit Test）的过程中，经常会出现这种情况，被测系统调用了第三方依赖组件（depended-on component，简称DOC），而这个依赖组件你无法控制（或者还未实现）。为了让单元测试顺利进行，就必须摆脱对这些对象的依赖，通常会使用测试替代（Test Double）技术合理隔离（摆脱）外部依赖项，进而以提高单元测试效率。

​	我们的系统可能有非常庞大的对象关联结构，通过测试替代可以将庞大的结构分离开来。这样在单元测试中就会更加明确是对哪个类进行测试。

​	以下是London Schools和传统模式的代码区别（从用3A原则设计）

```
public void Purchase_succeeds_when_enough_inventory()
{
	// Arrange
	var store = new Store();
	store.AddInventory(Product.Shampoo, 10);
	var customer = new Customer();
	// Act
	bool success = customer.Purchase(store, Product.Shampoo, 5);
	// Assert
	Assert.True(success);
	Assert.Equal(5, store.GetInventory(Product.Shampoo));	//断言减少后的库存数量
}

public void Purchase_fails_when_not_enough_inventory()
{
	// Arrange
	var store = new Store();
	store.AddInventory(Product.Shampoo, 10);
	var customer = new Customer();
	// Act
	bool success = customer.Purchase(store, Product.Shampoo, 15);
	// Assert
	Assert.False(success);
	Assert.Equal(10, store.GetInventory(Product.Shampoo));	//断言库存数量有没有改变，如果失败了理论上是不改变的
}

public enum Product
{
	Shampoo,
	Book
}
```

看上面的代码，Customer是要测试的部分，而Store是合作类。因为customer.Purchase()需要Store作为参数，所以需要Store才能进行编译。另外断言阶段也需要检测库存结果是否符合预期

我们可以看到，传统风格的单元测试并没有用测试替代来取代Store类，而是用了已有的实例。这样写的单元测试的结果就是，它不是只测试Customer类，而是同时也测试了Store类。所以显然如果Store类有任何BUG，都会让Customer类的测试失败，即使本来是正确的，这就是两个类没有隔离测试的问题。

接下来用London风格来修改测试代码，所有面向对象的语言都有很多类似的mock框架，如Java的Mockito, JMock, or EasyMock。值得注意的是，Mock只是一种特定的依赖，而测试替代是一个概括性的术语，它描述了测试中各种非生产就绪的、虚拟的依赖项。

修改后的代码如下：

```
public void Purchase_succeeds_when_enough_inventory()
{
	// Arrange
	var storeMock = new Mock<IStore>();
	storeMock
		.Setup(x => x.HasEnoughInventory(Product.Shampoo, 5))
		.Returns(true);
	var customer = new Customer();
	// Act
	bool success = customer.Purchase(
		storeMock.Object, Product.Shampoo, 5);
	// Assert
	Assert.True(success);
	storeMock.Verify(
		x => x.RemoveInventory(Product.Shampoo, 5),
		Times.Once);
}
[Fact]
public void Purchase_fails_when_not_enough_inventory()
{
	// Arrange
	var storeMock = new Mock<IStore>();
	storeMock
		.Setup(x => x.HasEnoughInventory(Product.Shampoo, 5))
		.Returns(false);
	var customer = new Customer();
	// Act
	bool success = customer.Purchase(
	storeMock.Object, Product.Shampoo, 5);
	// Assert
	Assert.False(success);
	storeMock.Verify(
		x => x.RemoveInventory(Product.Shampoo, 5),
		Times.Never);
}
```

我们注意到，在Arrange阶段，我们没有实例化Store类，取而代之的是用一个·创建了一个Mock<T>去替代它。另外，我们直接告诉mock怎么去响应调用HasEnoughInventory方法，用这个方法来取代添加库存的操作，这样做的好处是，无论Store是否有问题，mock都会返回测试需要的结果。我们再关注断言阶段，相应的，我们也不再通过检测获取Store类的库存值后比较的方法来做测试，而是通过Customer和Store的交互来检测，如果Customer执行成功，那么他至少会执行一次Remove方法，反之则一次都不会执行。通过以上的方式，这个单元测试就只针对Customer类进行了测试，实现了与Store类的完全解耦。

优点总结：

- 更好的粒度
- 更易于测试更大的互连类图进行单元
- 发现问题所在很快

### Classical School

​	还有一种可以解释隔离属性的方式——传统方式。在传统方式中，并不是代码需要以隔离的方式进行测试，而是单元测试本身需要互相隔离。你可以并行、串行或者以任何方式你最需要的方式运行而都不会担心他们会互相影响彼此的输出。

​	将测试彼此隔离意味着可以一次执行多个类，只要它们都驻留在内存中并且不使用共享的区域，测试可以进行通信并影响彼此的执行上下文。 这种共享状态的典型例子是外部依赖——数据库、文件系统等。例如，一个测试向数据库中插入了一条数据，另外一个测试可能删除了一条数据，如果他们前后执行的话，第一个测试可能明明正常运行了，断言的结果却是失败的。

​	所以这样对隔离问题提出了一个更为温和的解决方式，你仍然可以使用测试替代或者是mock，但是只是通常使用在那些有共享区域的部分。要注意的是，共享依赖只是在单元测试之间共享，而不是对于被测试的代码共享。某种意义上说，只要你可以每个测试创建一个新的实例，你就可以不用共享单例的依赖项。图如下：

​	![image-20210723205800615](C:\Users\zhanghaoran25\AppData\Roaming\Typora\typora-user-images\image-20210723205800615.png)

​	替换共享的外部依赖的好处是外部依赖（如数据库）往往调用速度会慢于私有依赖，原因是外部依赖往往常驻在程序之外，而私有依赖往往加载在内存之中，他们的调用速度有着明显区别，这样也会满足根据单元测试的快速执行的原则。

综上所述，London学派和传统学派的根本区别就是隔离方式的差异。London学派将被测系统与其合作者相分离，而传统学派则是侧重于单元测试之间的隔离。两者的主要区别有以下三点：

- 隔离的要求

- 怎么定义一个被测的代码块

- 解决依赖的方法

  总结如下：

|          | 隔离单位 | 单元定义       | 测试替代作用               |
| -------- | -------- | -------------- | -------------------------- |
| 伦敦学派 | 单元     | 一个类         | 除了不可变的依赖以外的一切 |
| 传统学派 | 单元测试 | 一个或者一组类 | 共享依赖                   |

那么什么是不可变依赖呢，

```
public void Purchase_succeeds_when_enough_inventory()
{
	// Arrange
	var storeMock = new Mock<IStore>();
	storeMock
		.Setup(x => x.HasEnoughInventory(Product.Shampoo, 5))
		.Returns(true);
	var customer = new Customer();
	// Act
	bool success = customer.Purchase(
		storeMock.Object, Product.Shampoo, 5);
	// Assert
	Assert.True(success);
	storeMock.Verify(
		x => x.RemoveInventory(Product.Shampoo, 5),
		Times.Once);
}
```

在上面这段代码中，Product实例就是不可变的，而只有 Store 包含一个内部状态，可以随着时间的推移而变化，因此这里只替换了 Store 实例，这个就好比你不会替换常量5一样，因为5本身静态不可改变的。

![image-20210726100412381](C:\Users\zhanghaoran25\AppData\Roaming\Typora\typora-user-images\image-20210726100412381.png)

### 共享和进程外依赖之间的关系 

​	共享但不在进程外的依赖关系的一个例子是单例（所有测试重用的实例）或类中的静态字段。 数据库既是共享的也是进程外的，它驻留在主进程之外并且是可变的。 只读 API 是进程外的，但不是共享的，因为测试用例不能修改它，因此并不能影响彼此的执行流程。

​	举个例子，如果有一个 API 返回产品的目录，只要 API 不公开更改目录的功能，这就不是共享依赖项。 虽然这样的依赖项是易变的，并且位于应用程序的边界之外，但是由于测试用例不能影响它返回的数据，因此它不会被共享。 但这并不意味着必须在包含这样的依赖项。 在大多数情况下，您仍然需要将其替换为测试替代以保持测试速度。但是如果进程外依赖足够快并且与其连接稳定，则可以很好地将其作用在测试中。

### AAA(Arrange、Act、Assert)原则

​	Arrange是输入条件，将被测系统 (SUT) 及其依赖项初始化，Act是执行的动作，调用 SUT 上的方法，传递准备好的依赖项，并捕获输出值。Assert是单元测试的断言，用于验证结果。一般我们倾向于将测试代码分为这三部分，每一部分放在一起。

​	3A模式为单元测试提供了一个更简单，更统一的结构，会使单元测试更有可读性。

#### 测试驱动开发（TDD）

​	开发功能之前尝试创建失败的测试，这时候我们对功能的行为还不够了解。思考和概述我们对行为的期望，然后弄清楚如何开发系统以满足这种期望。这种技术可能看起来违反常理，但这就是我们解决问题的方式。我们首先考虑目标：对于特定行为我们应该做什么。然后再解决问题。在写业务代码之前写下断言只是这种思维过程的形式化。但是，这条准则仅适用于遵循 TDD 的情况——即在生产代码之前编写测试

#### 避免多个Arrange、Act和Assert

![image-20210726100629347](C:\Users\zhanghaoran25\AppData\Roaming\Typora\typora-user-images\image-20210726100629347.png)

​	当看到多个由Assert分隔的Act部分时，可能还有Arrange部分，这意味着测试验证了多个行为单元。 正如我们之前讨论的，这样的测试不再是单元测试而是集成测试。我们要尝试避免这样的测试结构。 保持在单元测试的范围内。 如果看到包含一系列操作和断言的测试，要进行重构， 将每个动作提取到它自己的测试中。

#### 避免测试中的if语句

​	我们要求单元测试是简单的步骤序列，没有分支，if语句只会带来额外的维护成本并使测试代码更难阅读和理解

#### AAA各个部分应该有多大呢

​	首先Arrange部分是最大的，可以等于剩下两者的总和，但是如果太多的话，最好还是提取到一个测试类的私有方法中，或者分离提炼出工厂类。

​	其次，要注意大于一行的Act部分，Act通常只有一行代码，如果这个行为有多行代码，代表被测代码的公共API有问题。如下代码：

```
public void Purchase_succeeds_when_enough_inventory()
{
	// Arrange
	var store = new Store();
	store.AddInventory(Product.Shampoo, 10);
	var customer = new Customer();
	// Act
	bool success = customer.Purchase(store, Product.Shampoo, 5);
	store.RemoveInventory(success, Product.Shampoo, 5);
	// Assert
	Assert.True(success);
	Assert.Equal(5, store.GetInventory(Product.Shampoo));
}
```

​	这段代码的问题在于 Customer 类的接口类。 它不应该要求用户进行额外的方法调用。从业务的角度出发，一次成功的购买有两个结果：客户购买了产品同时商店的库存减少。这两个结果必须一起实现，这意味着应该有一个公共方法可以做到两件事。 否则，如果客户端代码调用第一种方法而不调用第二种方法，则存在不一致的空间，在这种情况下，客户获得产品，但其在商店中的可用数量不会减少

​	最后Assert阶段要考虑要维护多少个断言。单元测试中的单元是行为单元，而不是代码单元。 一个行为单元可以表现出多种结果，可以在一次测试中有多个断言。 但是话虽如此，要需要注意过大的断言部分，这可能是代码缺少抽象的迹象。

#### 被测试代码方法命名

将被测代码返回命名区分度的名字，使其与依赖项分开，如下：

```
public class CalculatorTests
{
	public void Sum_of_two_numbers()
	{
		// Arrange
		double first = 10;
		double second = 20;
		var sut = new Calculator();	//The calculator is now called sut
		// Act
		double result = sut.Sum(first, second);
		// Assert
		Assert.Equal(30, result);
	}
}
```

注意用空格或者注释分隔开三个部分

#### 注意重用测试代码

​	两个测试方法都要用到同样的逻辑配置，所以可以提取到构造函数中或者引入工厂类。

```
public class CustomerTests
{
	private readonly Store _store;
	private readonly Customer _sut;
	public CustomerTests()
	{
		_store = new Store();
		_store.AddInventory(Product.Shampoo, 10);
		_sut = new Customer();
	}
	public void Purchase_succeeds_when_enough_inventory()
	{
		bool success = _sut.Purchase(_store, Product.Shampoo, 5);
		Assert.True(success);
		Assert.Equal(5, _store.GetInventory(Product.Shampoo));
	}
	public void Purchase_fails_when_not_enough_inventory()
	{
		bool success = _sut.Purchase(_store, Product.Shampoo, 15);
		Assert.False(success);
		Assert.Equal(10, _store.GetInventory(Product.Shampoo));
	}
}
```

​	但是构造函数会导致测试之间高耦合，并且会降低可读性。如改变`_store.AddInventory(Product.Shampoo, 10)`这一行代码的数值，从10变化到15，多个测试方法将都会受到影响，这是不合理的，因为单元测试之间要保持独立性。

​	除非这个构造函数每个测试类都要用到，如数据库初始化，可以采用构造函数的方式。

```
public class CustomerTests
{
	public void Purchase_succeeds_when_enough_inventory()
	{
		Store store = CreateStoreWithInventory(Product.Shampoo, 10);
		Customer sut = CreateCustomer();
		bool success = sut.Purchase(store, Product.Shampoo, 5);
		Assert.True(success);
		Assert.Equal(5, store.GetInventory(Product.Shampoo));
	}
	public void Purchase_fails_when_not_enough_inventory()
	{
		Store store = CreateStoreWithInventory(Product.Shampoo, 10);
		Customer sut = CreateCustomer();
		bool success = sut.Purchase(store, Product.Shampoo, 15);
		Assert.False(success);
		Assert.Equal(10, store.GetInventory(Product.Shampoo));
	}
	private Store CreateStoreWithInventory(Product product, int quantity)
	{
		Store store = new Store();
		store.AddInventory(product, quantity);
		return store;
	}
	private static Customer CreateCustomer()
	{
		return new Customer();
	}
}
```

推荐用工厂类，有效地降低了耦合性，增加了可读性。

### 测试命名规范

​	用合适的英文描述测试的行为。如传递一个不合法的日期，得到不合法的返回结果的测试名可以叫做

​	`public void Delivery_with_invalid_date_should_be_considered_invalid()`

​	然后我们用实际的数据类别代替传递的参数，如过去的时间是不合法的，可以像这么写：

​	`public void Delivery_with_past_date_should_be_considered_invalid()`

​	然后去掉没有必要的词，将should be这样的词改成确定的含义，如下：

​	`public void Delivery_with_past_date_is_invalid()`

​	最后使得语句尽量符合英文语法，如下：

​	`public void Delivery_with_a_past_date_is_invalid()`

​	这样的名字直截了当，并清晰的描述了测试的行为。

​	在junit中，通常使用test_[被测代码方法名]来命名

### 优秀单元测试四大支柱

#### 防止回退的保护

​	回退是一种软件错误。它是指一个功能在某些代码修改后停止工作。要评估单元测试在防止回退中的表现，要通过以下几个方面：

1. 在测试中执行的代码量。通常，代码量越大，测试可以暴露回退bug的可能性越大。

2. 代码的复杂度和领域意义。一方面，业务核心功能中的bug是毁灭性的；另一方面，琐碎简单的代码发生回归bug的可能不大。

   要制定良好的回归保护措施，不然将无法维持项目的可持续发展

   此外，除了自己写的代码以外，没有写的代码也要算在内，例如，库、框架和项目中使用的任何外部系统。 这些代码对你的软件工作的影响几乎和你自己的代码一样大。为了获得最好的保护，必须将这些库、框架和外部系统纳入测试范围，以检查你的软件对这些依赖关系的假设是否正确。

#### 对重构的抵抗力

​	一个好的单元测试要可以承受底层应用代码的重构而不失败。

​	如果我们写完开发完新功能，测试也通过了，然后我们做一些重构和小的修改。改动后一切都比以前更好了，唯独测试失败了。你开始怀疑你的代码出了问题，但是发现代码没有任何问题，而是测试代码写法有问题导致在底层代码变动的时候就是会测试失败。这样的问题叫做假阳性错误。假阳性错误带来的主要问题是削弱对测试报错的敏感度。

​	假阳性出现的主要原因就是测试与被测系统细节结合过多，要做到测试和代码细节脱钩。如下：

![image-20210727151013156](C:\Users\zhanghaoran25\AppData\Roaming\Typora\typora-user-images\image-20210727151013156.png)

#### 快速反馈

​	快速得到测试的结果

#### 可维护性

​	

