require 'rubygems'
require 'watir-webdriver'
require 'test/unit'

class MainTest < Test::Unit::TestCase
    def test_main_route
        b = Watir::Browser.new
        b.goto 'http://localhost:3000'
        b.text_field(:name => 'ip').set "180.117.51.245"
        btn = b.button(:type => 'submit')
        assert btn.exists?
        btn.click
        Watir::Wait.until { b.text.include? '苏州' }
        assert b.text.include? "180.117.51.245"
        assert b.text.include? "苏州"
        b.close
    end
end
