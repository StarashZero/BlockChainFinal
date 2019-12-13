import Vue from 'vue'
import Vuex from 'vuex'
import Cookie from 'vue-cookies'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    username: Cookie.get('username'),
    password: Cookie.get('password')
  },
  mutations: {
    // 临时存储用户账号和密码
    saveUser: function (state, data) {
      state.username = data.username
      state.password = data.password
      Cookie.set('username', data.username, '20min')
      Cookie.set('password', data.password, '20min')
    },
    // 清空cookie
    clearUser: function (state) {
      state.username = null
      state.token = null
      Cookie.remove('username')
      Cookie.remove('password')
    }
  }
})
