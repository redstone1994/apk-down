<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="utf-8">
    <title>测试包下载</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="/css/layui.css" media="all">
    <script src="http://libs.baidu.com/jquery/2.1.4/jquery.min.js"></script>
    <!-- 注意：如果你直接复制所有代码到本地，上述css路径需要改成你本地的 -->
</head>
<body>

<div style="text-align: center;width: auto;height: auto">
    <table class="layui-table" lay-data="{data: ${data}, id:'idTest'}"
           lay-filter="demo">
        <thead>
        <tr>
            <th lay-data="{field:'item', width:250, align:'center', sort: true}">项目</th>
            <th lay-data="{field:'fileName', width:250, align:'center', sort: true}">包名</th>
            <th lay-data="{field:'time', width:250, align:'center', sort: true}">时间</th>
            <th lay-data="{fixed: 'right', width:100, align:'center', toolbar: '#barDemo'}">下载</th>
        </tr>
        </thead>
    </table>
</div>
<script type="text/html" id="barDemo">
    <a class="layui-btn layui-btn-primary layui-btn-xs" lay-event="detail">下载</a>
</script>

<script src="/layui.js" charset="utf-8"></script>

<script>
    layui.use('table', function () {
        var table = layui.table;
        //监听工具条
        table.on('tool(demo)', function (obj) {
            var data = obj.data;
            if (obj.event === 'detail') {
                layer.msg('开始下载');
                var name = data.fileName;
                var path = data.filePath;
                window.location.href = "./down" + "?" + "fileName=" + name + "&filePath=" + path;
            }
        });


        $('.demoTable .layui-btn').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });
    });
</script>

</body>
</html>