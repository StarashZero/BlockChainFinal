<template>
  <div class="content">
    <el-card class="box-card1">
      <div slot="header" class="clearfix">
        <span>个人信息</span>
      </div>
      <div>
        <span class='info'>用户名</span>
        <br>
        <span class='info'>{{username}}</span>
      </div>
      <br>
      <div>
        <div>
          <span class='info'>余额</span>
          <br>
          <span class='info'>{{account}}</span>
        </div>
      </div>
      <br>
      <div>
        <span class='info'>是否为银行</span>
        <br>
        <span class='info'>{{bank}}</span>
      </div>
      <br>
      <div>
        <span class='info'>待还账单ID</span>
        <br>
        <span class='info'>{{rId}}</span>
      </div>
      <br>
      <div>
        <span class='info'>持有账单ID</span>
        <br>
        <span class='info'>{{bId}}</span>
      </div>
      <br>
      <div>
        <el-button type="primary" @click="cilckQuit()">退出登录</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'usercontent',
  data () {
    return {
      username: '',
      bank: '',
      account: '',
      rId: '',
      bId: ''
    }
  },
  mounted: function () {
    // 当组件一加载就执行的函数
    this.init()
  },
  methods: {
    init: function () {
      var that = this
      var username = this.$store.state.username
      if (username == null) {
        // 未登录时给予提示
        alert('未登录')
      } else {
        // 向后端发送GET请求，获得用户信息
        this.$axios.request({
          url: 'http://localhost:8080/user/' + username + '/content',
          method: 'GET'
        }).then(function (response) {
          console.log(response.data)
          if (response.data.status === 'ok') {
            that.username = response.data.username
            that.account = response.data.account
            that.bank = response.data.bank
          } else {
            alert('查询失败')
          }
        })
        this.$axios.request({
          url: 'http://localhost:8080/user/' + username + '/receipts',
          method: 'GET'
        }).then(function (response) {
          console.log(response.data)
          if (response.data.status === 'ok') {
            that.rId = response.data.receipts
          } else {
            alert('查询失败')
          }
        })
        this.$axios.request({
          url: 'http://localhost:8080/user/' + username + '/bills',
          method: 'GET'
        }).then(function (response) {
          console.log(response.data)
          if (response.data.status === 'ok') {
            that.bId = response.data.bills
          } else {
            alert('查询失败')
          }
        })
      }
    },
    cilckQuit: function () {
      // 退出登录
      this.$store.commit('clearUser', {
      })
      this.$router.push('/login')
    }
  }
}
</script>

<style>
  .box-card1 {
    position:absolute;
    top: 5%;
    width: 400px;
    left: 50%;
    margin-left:-200px;
  }
</style>
