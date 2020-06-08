<#assign base=request.contextPath />
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, viewport-fit=cover">
    <title>测试包下载</title>
    <!-- 引入样式文件 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/vant@2.2/lib/index.css">
</head>

<body>

<!-- 引入 Vue 和 Vant 的 JS 文件 -->
<script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/vant@2.8/lib/vant.min.js"></script>

<div id="head"></div>
<div id="app" class="center" style="margin-top: 20%">

<#--    <template>-->
        <div v-for="value in apklist" style="margin-top: 20px">
            name:{{value.fileName}}
            <van-row type="flex" justify="center">
                <van-col>
                    <a v-bind:href="" v-if="">
                        <van-image round width="100" height="100" src="${base}/uni.png"/>
                    </a>
                    <a v-bind:href="" v-else-if="">
                        <van-image round width="100" height="100" src="${base}/iunk.png"/>
                    </a>
                    <a v-bind:href="" v-else-if="">
                        <van-image round width="100" height="100" src="${base}/mid.png"/>
                    </a>
                </van-col>
            </van-row>
        </div>
<#--    </template>-->

</div>


<script>

    new Vue({
        el: "#head",
        template: '<van-nav-bar title="请选择" fixed="true"/>'
    })

    var Main = {
        data() {
            return{
                apklist: ${data}
            }
        }
    }

    var Ctor = Vue.extend(Main)
    new Ctor().$mount('#app')

    // 通过 CDN 引入时不会自动注册 Lazyload 组件
    // 可以通过下面的方式手动注册
    Vue.use(vant.Lazyload);
</script>

</body>
</html>