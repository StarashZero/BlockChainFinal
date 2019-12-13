<template>
  <div id="app">
    <el-menu
      :router="true"
      :default-active="this.$route.path"
      class="header"
      mode="horizontal"
      background-color="#545c64"
      text-color="#fff"
      active-text-color="#ffd04b">
      <h1 id="ApplyChain">Fisco-bcos 供应链</h1>
      <el-menu-item index="/query" >查询账单</el-menu-item>
      <el-menu-item index="/sign" v-if="this.$store.state.username" >签署账单</el-menu-item>
      <el-menu-item index="/transfer" v-if="this.$store.state.username" >转让账单</el-menu-item>
      <el-menu-item index="/finance" v-if="this.$store.state.username" >融资</el-menu-item>
      <el-menu-item index="/settle" v-if="this.$store.state.username" >结算</el-menu-item>
      <el-menu-item index="/user" style='float:right' v-if="this.$store.state.username">{{ this.$store.state.username }}</el-menu-item>
      <div v-else="">
        <el-menu-item class = "log" index="/register" style='float:right'>注册</el-menu-item>
        <el-menu-item class = "log" index="/login" style='float:right'>登陆</el-menu-item>
      </div>
    </el-menu>
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
  body {
    margin: 0;
  }

  .log {
    height: 60px;
  }

  #ApplyChain {
    line-height: 1.2;
    height: 36.8px;
    float: left;
    font-size: 30px;
    margin-block-start: 10px;
    margin-block-end: 10px;
    margin-inline-start: 0px;
    margin-inline-end: 0px;
    color: #FFF;
    width: 300px;
  }

  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
  }
</style>
