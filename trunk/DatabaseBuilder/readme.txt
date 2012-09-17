数据库加密方法：

将db文件copy到DatabaseBuilder\assets

编译运行DatabaseBuilder

在data\data\com.dreamori.databasebuilder\databases\中拿到加密后的文件

把加密好的数据库文件libFoodFuncion.so放在food的\assets目录中

在DatabaseHelper类中
去掉static String srcDBName = "libFoodFuncion.so";这一行的注释
注释static String srcDBName = "food.db";这一行
去掉String uri = (String) db.getdb()+"SQLITE_OPEN_READWRITE";这一行的注释
去掉getInstance函数中db.key(db.dbkey());这一行的注释

编译，运行即可