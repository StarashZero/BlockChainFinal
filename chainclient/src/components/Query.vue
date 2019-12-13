<template>
  <div class="query">
    <h1>{{ msg }}</h1>
    <div>
      <el-input v-model="rId" placeholder="账单ID" class='inputClass'></el-input>
    </div>
    <br>
    <div>
      <el-input v-model="name" placeholder="账单拥有人(可选)" class='inputClass'></el-input>
    </div>
    <br>
    <br>
    <div>
      <el-button type="primary" @click="cilckQuery()">查询</el-button>
    </div>
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>账单信息</span>
      </div>
      <div>
        <span class='info'>账单ID</span>
        <br>
        <span class='info'>{{tId}}</span>
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
        <span class='info'>金额</span>
        <br>
        <span class='info'>{{total}}</span>
      </div>
      <br>
      <div>
        <span class='info'>是否有效</span>
        <br>
        <span class='info'>{{used}}</span>
      </div>
      <br>
      <div>
        <span class='info'>持有人</span>
        <br>
        <span class='info'>{{creditor}}</span>
      </div>
      <br>
      <div>
        <span class='info'>持有金额</span>
        <br>
        <span class='info'>{{amount}}</span>
      </div>
      <br>
      <div>
        <span class='info'>可用金额</span>
        <br>
        <span class='info'>{{canUse}}</span>
      </div>
      <br>
      <br>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Query',
  data () {
    return {
      msg: '查询账单',
      rId: '',
      name: '',
      obligor: '',
      used: '',
      tId: '',
      creditor: '',
      amount: '',
      total: '',
      canUse: ''
    }
  },
  methods: {
    cilckQuery: function () {
      var that = this
      if (this.name === '') {
        // 向后端发生GET请求，查询基本信息
        this.$axios.request({
          url: 'http://localhost:8080/receipt/' + this.rId + '/content',
          method: 'GET'
        }).then(function (response) {
          console.log(response.data)
          if (response.data.status === 'ok') {
            that.tId = that.rId
            that.obligor = response.data.obligor
            that.used = response.data.used
            that.total = response.data.total
          } else {
            alert('查询失败')
          }
        })
      } else {
        // 向后端发生GET请求，查询详细信息
        this.$axios.request({
          url: 'http://localhost:8080/receipt/' + this.rId + '/bills/' + this.name,
          method: 'GET'
        }).then(function (response) {
          console.log(response.data)
          if (response.data.status === 'ok') {
            that.tId = that.rId
            that.obligor = response.data.obligor
            that.used = response.data.used
            that.total = response.data.total
            that.creditor = response.data.creditor
            that.amount = response.data.amount
            that.canUse = response.data.canUse
          } else {
            alert('查询失败')
          }
        })
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
