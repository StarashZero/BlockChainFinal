<template>
  <div class="finance">
    <br>
    <div slot="header" class="clearfix">
      <span>融资</span>
    </div>
    <br>
    <el-form class="table" ref="form" :model="form" label-width="80px">
      <el-form-item label="银行名称">
        <el-input v-model="form.name"></el-input>
      </el-form-item>
      <el-form-item label="账单ID">
        <el-input v-model="form.rId"></el-input>
      </el-form-item>
      <el-form-item label="金额">
        <el-input v-model="form.amount"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">提交融资</el-button>
      </el-form-item>
    </el-form>
    <br>
    <br>
    <br>
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>融资信息</span>
      </div>
      <div>
        <span class='info'>账单ID</span>
        <br>
        <span class='info'>{{rId}}</span>
      </div>
      <br>
      <div>
        <div>
          <span class='info'>银行</span>
          <br>
          <span class='info'>{{from}}</span>
        </div>
      </div>
      <br>
      <div>
        <span class='info'>融资者</span>
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
      // 向后端发生POST请求，结算
      var that = this
      this.$axios.request({
        headers: {
          'Content-Type': 'application/json;charset=UTF-8'
        },
        url: 'http://localhost:8080/receipt/finance',
        method: 'POST',
        data: JSON.stringify({
          creditor: username,
          password: password,
          bank: this.form.name,
          amount: this.form.amount,
          rId: this.form.rId
        }),
        responseType: 'json'
      }).then(function (response) {
        console.log(response.data)
        if (response.data.status === 'ok') {
          that.rId = response.data.rId
          that.from = response.data.bank
          that.to = response.data.creditor
          that.amount = response.data.amount
        } else {
          alert('融资失败')
        }
      })
    }
  }
}
</script>
