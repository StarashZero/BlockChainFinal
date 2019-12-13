import Vue from 'vue'
import Router from 'vue-router'
import Index from '@/components/Index'
import Login from '@/components/Login'
import Rigister from '@/components/Register'
import UserContent from '@/components/UserContent'
import Query from '@/components/Query'
import Sign from '@/components/Sign'
import Transfer from '@/components/Transfer'
import Finance from '@/components/Finance'
import Settle from '@/components/Settle'
Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'index',
      component: Index
    },
    {
      path: '/index',
      name: 'index',
      component: Index
    },
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/register',
      name: 'register',
      component: Rigister
    },
    {
      path: '/user',
      name: 'user',
      component: UserContent
    },
    {
      path: '/query',
      name: 'query',
      component: Query
    },
    {
      path: '/sign',
      name: 'sign',
      component: Sign
    },
    {
      path: '/transfer',
      name: 'transfer',
      component: Transfer
    },
    {
      path: '/finance',
      name: 'finance',
      component: Finance
    },
    {
      path: '/settle',
      name: 'settle',
      component: Settle
    }
  ],
  mode: 'history'
})
