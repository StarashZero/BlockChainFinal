<template>
  <div class="sign">
    <br>
    <div slot="header" class="clearfix">
      <span>签署账单</span>
    </div>
    <br>
    <el-form class="table" ref="form" :model="form" label-width="80px">
      <el-form-item label="接受者">
        <el-input v-model="form.name"></el-input>
      </el-form-item>
      <el-form-item label="账单金额">
        <el-input v-model="form.amount"></el-input>
      </el-form-item>
      <el-form-item label="证明者">
        <el-input v-model="form.certifier"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">提交账单</el-button>
      </el-form-item>
    </el-form>
    <br>
    <br>
    <br>
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>账单信息</span>
      </div>
      <div>
        <span class='info'>账单ID</span>
        <br>
        <span class='info'>{{rId}}</span>
      </div>
      <br>
      <div>
        <div>
          <span class='info'>负债人</span>
          <br>
          <span class='info'>{{obligor}}</span>
        </div>
      </div>
      <br>
      <div>
        <span class='info'>债权人</span>
        <br>
        <span class='info'>{{creditor}}</span>
      </div>
      <br>
      <div>
        <span class='info'>待还金额</span>
        <br>
        <span class='info'>{{amount}}</span>
      </div>
      <br>
      <div>
        <span class='info'>证明人</span>
        <br>
        <span class='info'>{{certifier}}</span>
      </div>
      <br>
      <br>
    </el-card>
  </div>
</template>

<script>
export default {
  data () {
    return {
      rId: '',
      obligor: '',
      creditor: '',
      amount: '',
      certifier: '',
      form: {
        name: '',
        amount: '',
        certifier: ''
      }
    }
  },
  methods: {
    onSubmit () {
      var username = this.$store.state.username
      var password = this.$store.state.password
      if (username == null) {
        // 未登录时给予提示
        alert('未登录')
        return
      }
      // 向后端发生POST请求，签署账单
      var that = this
      this.$axios.request({
        headers: {
          'Content-Type': 'application/json;charset=UTF-8'
        },
        url: 'http://localhost:8080/receipt/sign',
        method: 'POST',
        data: JSON.stringify({
          obligor: username,
          password: password,
          creditor: this.form.name,
          amount: this.form.amount,
          certifier: this.form.certifier
        }),
        responseType: 'json'
      }).then(function (response) {
        console.log(response.data)
        // 传送数据到store.js的saveToken，并回到主页
        if (response.data.status === 'ok') {
          that.rId = response.data.rId
          that.obligor = response.data.obligor
          that.creditor = response.data.creditor
          that.amount = response.data.amount
          that.certifier = response.data.certifier
        } else {
          alert('账单签署失败')
        }
      })
    }
  }
}
</script>

<style>
  .table {
    width: 400px;
    position:absolute;
    left:50%;
    margin-left:-225px;
  }
  .box-card {
    position:absolute;
    top: 50%;
    width: 400px;
    left: 50%;
    margin-left:-200px;
  }
</style>
