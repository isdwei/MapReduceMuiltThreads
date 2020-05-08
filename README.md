# MapReduceMuiltThreads

* 可用于海量数据的计算
* 主要包括Map、Reduce、Context、Driver类
* 只需继承Mapper，Reducer类，编写相应的计算逻辑，
并修改对应的配置文件参数即可部署运行

工作流程：

* 首先加载配置文件，从配置文件中读取的MyMapper、MyReducer的文件位置，和输入、输出文件路径、Reduce任务个数，并保存在一个HashMap中。
* 初始化Map任务，包括：
  * 检查文件路径是否正确
  * 删除临时文件目录
* 从指定的输入路径下，依次读取文件，开始切分输入文件
  * 从MapReduceFactory类中获取一个MapTask，一个MapContext
  * 对每一个文件按行读，并调用MapTask的map方法
  * map方法中执行的是用户自己定义的计算逻辑，传入参数是`行号，字符串，MapContext`
  * map方法最后调用context.write(K,V)
* MapContext继承自Context，主要用来将内存中的数据刷写入硬盘
  * write方法中，首先会根据key，计算出分区号，不同的Partition会被不同的线程计算
  * 通过ProtocolBuffer协议序列化KV对，存入一个内存缓冲区中，并计算当前缓冲区中数据大小
  * 当缓冲区大小大于指定值时，刷写入内存
* Reduce阶段开始，初始化ReduceLaunch类，ReduceLaunch类会创建指定大小的线程池，向线程池中提交实现了Runnable接口的ReduceTaskWapper
* 每个ReduceTaskWapper读取对应的临时文件到buffer中，并从MapReduceFactory类中获取一个ReduceTask，一个ReduceContext
* 将Buffer中数据反序列化，依次提取K-V对，并存入HashMap中
* 将HashMap中K与对应的所用V传入ReduceTask的reduce函数中，执行完Reduce中的计算逻辑，则在将结果K，V对写入磁盘。


后续计划：

* 写入序列化文件时还需一起写下索引 

  
