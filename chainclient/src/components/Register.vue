<template>
  <div class="register">
    <h1>{{ msg }}</h1>
    <div>
      <el-input v-model="userID" placeholder="用户名(6~18位英文字母、数字、下划线)" class='inputClass' @blur='userValidateCheck()'></el-input>
    </div>
    <span class='info' id='user'>{{userinfo}}</span>
    <br>
    <div>
      <el-input v-model="password" placeholder="密码(6~18位数字、大小写字母)" class='inputClass' type='password' @blur ='passwordCheck()'></el-input>
    </div>
    <span class='info' id='pswd1'>{{pswdinfo1}}</span>
    <br>
    <div>
      <el-input v-model="passwordConfirm" placeholder="再次输入密码确认" class='inputClass' type='password' @blur='passwordValidateCheck()'></el-input>
    </div>
    <span class='info' id='pswd2'>{{pswdinfo2}}</span>
    <br>
    <div>
      <el-input v-model="balance" placeholder="注册资金" class='inputClass' @blur='balanceValidateCheck()'></el-input>
    </div>
    <span class='info' id='email'>{{balanceinfo}}</span>
    <br>
    <span class='info' id='bank'>是否为银行?</span>
    <br>
    <br>
    <div>
      <el-radio v-model='radio' label="true">是</el-radio>
      <el-radio v-model='radio' label="false">否</el-radio>
    </div>
    <br>
    <div>
      <el-button type="primary" @click='clickRegister()'>注册</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'register',
  data () {
    return {
      msg: '注册',
      userID: '',
      password: '',
      passwordConfirm: '',
      balance: '',
      userinfo: '',
      pswdinfo1: '',
      pswdinfo2: '',
      balanceinfo: '',
      radio: 'false'
    }
  },
  methods: {
    clickRegister: function () {
      // 合法性验证
      if (!this.userValidateCheck() || !this.passwordCheck() || !this.passwordValidateCheck() || !this.balanceValidateCheck()) {
        alert('请正确填写信息')
        return
      }
      // 向后端发生POST请求，注册
      var that = this
      this.$axios.request({
        url: 'http://localhost:8080/user/register',
        headers: {
          'Content-Type': 'application/json;charset=UTF-8'
        },
        method: 'POST',
        data: JSON.stringify({
          username: this.userID,
          password: this.password,
          bank: this.radio,
          amount: this.balance
        }),
        responseType: 'json'
      }).then(function (response) {
        console.log(response.data)
        if (response.data.status === 'ok') {
          that.$store.commit('saveUser', {
            username: that.userID,
            password: response.data.password
          })
          that.$router.push('/')
        } else {
          alert('注册失败')
        }
      })
    },
    userValidateCheck: function () {
      if (!(/^[0-9a-zA-Z_]{6,18}$/.test(this.userID))) {
        this.userinfo = '用户名格式不正确'
        return false
      } else {
        this.userinfo = ''
        return true
      }
    },
    passwordCheck: function () {
      if (!(/^[0-9a-zA-Z]{6,18}$/.test(this.password))) {
        this.pswdinfo1 = '密码格式不正确'
        return false
      } else {
        this.pswdinfo1 = ''
        return true
      }
    },
    passwordValidateCheck: function () {
      if (this.password !== this.passwordConfirm) {
        this.pswdinfo2 = '两次密码输入不相同'
        return false
      } else {
        this.pswdinfo2 = ''
        return true
      }
    },
    balanceValidateCheck: function () {
      if (!(/^[0-9]+$/.test(this.balance))) {
        this.balanceinfo = '资金格式不正确'
        return false
      } else {
        this.balanceinfo = ''
        return true
      }
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
