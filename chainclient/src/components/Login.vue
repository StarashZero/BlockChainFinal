<template>
  <div class="login">
    <h1>{{ msg }}</h1>
    <div>
      <el-input v-model="userID" placeholder="用户名" class='inputClass'></el-input>
    </div>
    <br>
    <div>
      <el-input v-model="password" placeholder="密码" class='inputClass' type='password'></el-input>
    </div>
    <br>
    <div>
      <el-button type="primary" @click="cilckLogin()">登录</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'login',
  data () {
    return {
      msg: '登录',
      userID: '',
      password: ''
    }
  },
  methods: {
    cilckLogin: function () {
      // 向后端发生POST请求，登录
      var that = this
      this.$axios.request({
        headers: {
          'Content-Type': 'application/json;charset=UTF-8'
        },
        url: 'http://localhost:8080/user/login',
        method: 'POST',
        data: JSON.stringify({
          username: this.userID,
          password: this.password
        }),
        responseType: 'json'
      }).then(function (response) {
        console.log(response.data)
        if (response.data.status === 'ok') {
          that.$store.commit('saveUser', {
            username: that.userID,
            password: that.password
          })
          that.$router.push('/')
        } else {
          alert('登录失败')
        }
      })
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  body{
    font-family: "Helvetica Neue",Helvetica,"PingFang SC","Hiragino Sans GB","Microsoft YaHei","微软雅黑",Arial,sans-serif;
  }
  h1, h2 {
    font-weight: normal;
  }
  ul {
    list-style-type: none;
    padding: 0;
  }
  li {
    display: inline-block;
    margin: 0 10px;
  }
  a {
    color: #42b983;
  }
  .inputClass{
    width:300px
  }
</style>
