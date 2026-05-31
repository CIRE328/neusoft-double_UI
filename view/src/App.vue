<!-- App：根组件，承载路由视图 -->
<template>
  <router-view />
</template>

<script setup>
/**
 * 根组件
 * 作为应用入口容器，通过 router-view 渲染当前路由对应的页面
 */
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
html, body, #app {
  height: 100%;
  width: 100%;
}
</style>
