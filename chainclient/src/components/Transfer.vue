<template>
  <div class="transfer">
    <br>
    <div slot="header" class="clearfix">
      <span>转让账单</span>
    </div>
    <br>
    <el-form class="table" ref="form" :model="form" label-width="80px">
      <el-form-item label="接受者">
        <el-input v-model="form.name"></el-input>
      </el-form-item>
      <el-form-item label="账单ID">
        <el-input v-model="form.rId"></el-input>
      </el-form-item>
      <el-form-item label="金额">
        <el-input v-model="form.amount"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">提交转让</el-button>
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
          <span class='info'>发送者</span>
          <br>
          <span class='info'>{{from}}</span>
        </div>
      </div>
      <br>
      <div>
        <span class='info'>接受者</span>
        <br>
        <span class='info'>{{to}}</span>
      </div>
      <br>
      <div>
        <span class='info'>金额</span>
        <br>
        <span class='info'>{{amount}}</span>
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
      from: '',
      to: '',
      amount: '',
      form: {
        name: '',
        amount: '',
        rId: ''
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
      // 想后端发生POST请求，进行账单转让
      var that = this
      this.$axios.request({
        headers: {
          'Content-Type': 'application/json;charset=UTF-8'
        },
        url: 'http://localhost:8080/receipt/transfer',
        method: 'POST',
        data: JSON.stringify({
          sender: username,
          password: password,
          receiver: this.form.name,
          amount: this.form.amount,
          rId: this.form.rId
        }),
        responseType: 'json'
      }).then(function (response) {
        console.log(response.data)
        if (response.data.status === 'ok') {
          that.rId = response.data.rId
          that.from = response.data.from
          that.to = response.data.to
          that.amount = response.data.amount
        } else {
          alert('账单转让失败')
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
